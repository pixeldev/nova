package ml.stargirls.nova.paper.player.teleport;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.location.ServerLocationModel;
import ml.stargirls.nova.paper.player.server.PlayerServerModel;
import ml.stargirls.nova.paper.player.server.PlayerServerSender;
import ml.stargirls.storage.dist.RemoteModelService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerTeleportHandlerImpl
	implements PlayerTeleportHandler {

	@Inject private MessageHandler messageHandler;
	@Inject private Logger logger;
	@Inject private Executor executor;

	@Inject private RemoteModelService<PlayerServerModel> playerServerModelService;
	@Inject private PlayerServerSender playerServerSender;
	@Inject private PlayerTeleportManager playerTeleportManager;

	private final String currentServerId;

	@Inject
	public PlayerTeleportHandlerImpl(@NotNull final ServerInfo serverInfo) {
		this.currentServerId = serverInfo.getId();
	}

	@Override
	public void teleport(@NotNull final Player player, @NotNull final Player target) {
		if (!player.teleport(target)) {
			messageHandler.sendIn(player, SendingModes.ERROR, "teleport.not-teleported");
			return;
		}

		messageHandler.sendIn(player, SendingModes.PING, "teleport.player-success");
	}

	@Override
	public void teleport(@NotNull final Player player, @NotNull final Location location) {
		player.teleportAsync(location)
			.whenComplete((teleported, throwable) -> {
				if (throwable != null) {
					messageHandler.sendIn(player, SendingModes.ERROR, "teleport.error");
					logger.error("Failed to teleport player " + player.getName(), throwable);
					return;
				}

				if (!teleported) {
					messageHandler.sendIn(player, SendingModes.ERROR, "teleport.not-teleported");
					return;
				}

				messageHandler.sendIn(player, SendingModes.PING, "teleport.location-success");
			});
	}

	@Override
	public void teleport(@NotNull final Player player, @NotNull final LocationModel locationModel) {
		try {
			teleport(player, locationModel.toLocation());
		} catch (IllegalArgumentException exception) {
			messageHandler.sendIn(player, SendingModes.ERROR, "teleport.error-parsing-location");
		}
	}

	@Override
	public void teleport(
		@NotNull final Player player,
		@NotNull final ServerLocationModel locationModel
	) {
		String serverId = locationModel.serverId();

		if (serverId.equals(currentServerId)) {
			teleport(player, locationModel.location());
		} else {
			CompletableFuture
				.runAsync(
					() -> {
						playerTeleportManager.registerPendingTeleport(PendingTeleportModel.location(
							player,
							locationModel.location()));

						try {
							playerServerSender.sendToServerSync(player, serverId);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					},
					executor
				)
				.whenComplete((unused, throwable) -> {
					if (throwable != null) {
						logger.error("Failed to teleport player " + player.getName(), throwable);
						messageHandler.sendIn(player, SendingModes.ERROR, "teleport.error");
					}
				});
		}
	}

	@Override
	public void teleport(@NotNull final Player player, @NotNull final UUID targetId) {
		Player target = Bukkit.getPlayer(targetId);

		if (target != null) {
			teleport(player, target);
			return;
		}

		playerServerModelService
			.find(targetId.toString())
			.thenAccept(playerServerModel -> {
				if (playerServerModel == null) {
					messageHandler.sendIn(player, SendingModes.ERROR, "user.not-found");
					return;
				}

				playerTeleportManager.registerPendingTeleport(PendingTeleportModel.target(
					player,
					targetId));

				try {
					playerServerSender.sendToServerSync(player, playerServerModel.getServerId());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			})
			.whenComplete((unused, throwable) -> {
				if (throwable != null) {
					logger.error("Failed to find player " + targetId, throwable);
					messageHandler.sendIn(player, SendingModes.ERROR, "teleport.error");
				}
			});
	}
}
