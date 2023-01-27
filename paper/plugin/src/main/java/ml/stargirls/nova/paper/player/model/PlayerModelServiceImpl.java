package ml.stargirls.nova.paper.player.model;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import ml.stargirls.nova.paper.player.resolve.PlayerTargetResolver;
import ml.stargirls.storage.ModelService;
import ml.stargirls.storage.dist.DelegatedCachedModelService;
import ml.stargirls.storage.model.Model;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerModelServiceImpl<T extends Model>
	extends DelegatedCachedModelService<T>
	implements PlayerModelService<T> {

	private final MessageHandler messageHandler;
	private final PlayerTargetResolver playerTargetResolver;

	public PlayerModelServiceImpl(
		@NotNull final Executor executor,
		@NotNull final ModelService<T> cacheModelService,
		@NotNull final ModelService<T> delegate,
		final MessageHandler messageHandler,
		final PlayerTargetResolver playerTargetResolver
	) {
		super(executor, cacheModelService, delegate);
		this.messageHandler = messageHandler;
		this.playerTargetResolver = playerTargetResolver;
	}

	@Override
	public @Nullable T getSelfSync(
		@NotNull final Player player,
		@Nullable final String notFoundPath
	) {
		T model = getSync(player.getUniqueId()
			                  .toString());

		if (model == null) {
			messageHandler.sendIn(
				player,
				SendingModes.ERROR,
				notFoundPath == null ?
				"user.self-not-found" :
				notFoundPath);
		}

		return model;
	}

	@Override
	public @Nullable T getOrFindSelfSync(
		@NotNull final Player player,
		@Nullable final String notFoundPath
	) {
		T model = getOrFindSync(player.getUniqueId()
			                        .toString());

		if (model == null) {
			messageHandler.sendIn(
				player,
				SendingModes.ERROR,
				notFoundPath == null ?
				"user.self-not-found" :
				notFoundPath);
		}

		return model;
	}

	@Override
	public @NotNull CompletableFuture<@Nullable T> getSelf(
		@NotNull final Player player,
		@Nullable final String notFoundPath
	) {
		return CompletableFuture.supplyAsync(() -> getSelfSync(player, notFoundPath), executor);
	}

	@Override
	public @NotNull CompletableFuture<@Nullable T> getOrFindSelf(
		@NotNull final Player player,
		@Nullable final String notFoundPath
	) {
		return CompletableFuture.supplyAsync(() -> getOrFindSelfSync(player, notFoundPath), executor);
	}

	@Override
	public @Nullable T getTargetSync(
		@NotNull final CommandSender sender,
		@NotNull final String targetId,
		@Nullable final String notFoundPath
	) {
		T model = getSync(targetId);

		if (model == null) {
			messageHandler.sendIn(
				sender,
				SendingModes.ERROR,
				notFoundPath == null ?
				"user.target-not-found" :
				notFoundPath);
		}

		return model;
	}

	@Override
	public @Nullable T getOrFindTargetSync(
		@NotNull final CommandSender sender,
		@NotNull final String targetId,
		@Nullable final String notFoundPath
	) {
		T model = getOrFindSync(targetId);

		if (model == null) {
			messageHandler.sendIn(
				sender,
				SendingModes.ERROR,
				notFoundPath == null ?
				"user.target-not-found" :
				notFoundPath);
		}

		return model;
	}

	@Override
	public @NotNull CompletableFuture<@Nullable T> getTarget(
		@NotNull final CommandSender sender,
		@NotNull final String targetId,
		@Nullable final String notFoundPath
	) {
		return CompletableFuture.supplyAsync(
			() -> getTargetSync(sender, targetId, notFoundPath),
			executor);
	}

	@Override
	public @NotNull CompletableFuture<@Nullable T> getOrFindTarget(
		@NotNull final CommandSender sender,
		@NotNull final String targetId,
		@Nullable final String notFoundPath
	) {
		return CompletableFuture.supplyAsync(
			() -> getOrFindTargetSync(sender, targetId, notFoundPath),
			executor);
	}

	@Override
	public @Nullable T resolveTargetAndGetSync(
		@NotNull final CommandSender sender,
		@NotNull final String targetName,
		@Nullable final String notFoundPath
	) {
		String targetId = playerTargetResolver.resolveIdStringSync(sender, targetName);

		if (targetId == null) {
			return null;
		}

		return getTargetSync(sender, targetId, notFoundPath);
	}

	@Override
	public @NotNull CompletableFuture<@Nullable T> resolveTargetAndGet(
		@NotNull final CommandSender sender,
		@NotNull final String targetName,
		@Nullable final String notFoundPath
	) {
		return CompletableFuture.supplyAsync(
			() -> resolveTargetAndGetSync(sender, targetName, notFoundPath),
			executor);
	}

	@Override
	public @Nullable T resolveTargetAndGetOrFindSync(
		@NotNull final CommandSender sender,
		@NotNull final String targetName,
		@Nullable final String notFoundPath
	) {
		String targetId = playerTargetResolver.resolveIdStringSync(sender, targetName);

		if (targetId == null) {
			return null;
		}

		return getOrFindTargetSync(sender, targetId, notFoundPath);
	}

	@Override
	public @NotNull CompletableFuture<@Nullable T> resolveTargetAndGetOrFind(
		@NotNull final CommandSender sender,
		@NotNull final String targetName,
		@Nullable final String notFoundPath
	) {
		return CompletableFuture.supplyAsync(
			() -> resolveTargetAndGetOrFindSync(sender, targetName, notFoundPath),
			executor);
	}
}
