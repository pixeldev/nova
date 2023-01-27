package ml.stargirls.nova.paper.player.connection;

import ml.stargirls.maia.paper.concurrent.PluginExecutor;
import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.storage.redis.channel.RedisChannel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class ConnectionProcessManager {

	@Inject private Executor executor;
	@Inject private ConnectionProcessRegistry registry;
	@Inject private Logger logger;
	@Inject private RedisChannel<ServerChangeRequest> channel;
	@Inject private MessageHandler messageHandler;
	@Inject private PluginExecutor pluginExecutor;

	/**
	 * The set of players that are currently being processed so that we don't process them twice when
	 * they finally disconnect from the server
	 */
	private final Set<UUID> processing = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public void handleConnect(@NotNull final Player player) {
		CompletableFuture
			.supplyAsync(
				() -> {
					try {
						ConnectionContext context = new ConnectionContextImpl(player);
						handleConnectSync(context);
						return context;
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				},
				executor
			)
			.thenCompose(context -> {
				if (context.isCancelled()) {
					return CompletableFuture.completedFuture(context);
				}

				return CompletableFuture
					       .supplyAsync(
						       () -> {
							       Queue<SyncRunnable> tasks = context.getSyncTasks();
							       while (!tasks.isEmpty()) {
								       try {
									       tasks.poll()
										       .run();
								       } catch (Exception e) {
									       throw new RuntimeException(e);
								       }
							       }

							       return context;
						       },
						       pluginExecutor
					       );
			})
			.whenCompleteAsync(
				(context, throwable) -> {
					if (throwable != null) {
						player.kick(messageHandler.get(player, "user.load-failed"));
						logger.warn(
							"Failed to handle connect " + "processes for " + player.getUniqueId(),
							throwable);
						return;
					}

					if (context.isCancelled()) {
						player.kick(messageHandler.get(player, "user.join-cancelled"));
					}
				},
				pluginExecutor
			);
	}

	public void handleConnectSync(@NotNull final ConnectionContext context)
		throws Exception {
		for (ConnectionProcess process : registry.getAll()) {
			process.processConnect(context);

			if (context.isCancelled()) {
				break;
			}
		}
	}

	public void handleDisconnect(@NotNull final Player player) {
		if (processing.remove(player.getUniqueId())) {
			// Disconnect processes were handled before,
			// so we don't need to do anything
			return;
		}

		CompletableFuture
			.runAsync(
				() -> {
					try {
						handleDisconnectSync(player);
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				},
				executor
			)
			.exceptionally(e -> {
				logger.warn("Failed to handle disconnect", e);
				return null;
			});
	}

	public void handleDisconnectSync(@NotNull final Player player)
		throws Exception {
		ConnectionContext context = new ConnectionContextImpl(player);

		for (ConnectionProcess process : registry.getAll()) {
			process.processDisconnect(context);
		}
	}

	public void handleSwitch(
		@NotNull final Player player,
		@NotNull final String destination
	) {
		CompletableFuture
			.runAsync(
				() -> {
					try {
						handleSwitchSync(player, destination);
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				},
				executor
			)
			.exceptionally(throwable -> {
				logger.error(
					"Failed to process disconnect for " + "player " + player.getName(),
					throwable);
				messageHandler.sendIn(player, SendingModes.ERROR, "server.change-denied");
				return null;
			});
	}

	public void handleSwitchSync(@NotNull final Player player, @NotNull final String destination)
		throws Exception {
		ConnectionContext context = new ConnectionContextImpl(player);

		for (ConnectionProcess process : registry.getAll()) {
			process.processSwitchServer(context, destination);
		}

		UUID playerId = player.getUniqueId();

		channel.sendMessage(
			new ServerChangeRequest(playerId, destination, true),
			ServerInfo.PROXY_SERVER.serverIdentifier());
		processing.add(playerId);
	}
}
