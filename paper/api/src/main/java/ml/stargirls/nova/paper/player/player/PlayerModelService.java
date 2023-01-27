package ml.stargirls.nova.paper.player.player;

import ml.stargirls.storage.AsyncModelService;
import ml.stargirls.storage.CachedAsyncModelService;
import ml.stargirls.storage.CachedModelService;
import ml.stargirls.storage.model.Model;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerModelService<T extends Model>
	extends CachedModelService<T>, CachedAsyncModelService<T>, AsyncModelService<T> {

	@Nullable T getSelfSync(@NotNull Player player, @Nullable String notFoundPath);

	@Nullable T getOrFindSelfSync(@NotNull Player player, @Nullable String notFoundPath);

	@NotNull CompletableFuture<@Nullable T> getSelf(
		@NotNull Player player,
		@Nullable String notFoundPath
	);

	@NotNull CompletableFuture<@Nullable T> getOrFindSelf(
		@NotNull Player player,
		@Nullable String notFoundPath
	);

	@Nullable T getTargetSync(
		@NotNull CommandSender sender,
		@NotNull String targetId,
		@Nullable String notFoundPath
	);

	default @Nullable T getTargetSync(
		@NotNull CommandSender sender,
		@NotNull UUID targetUuid,
		@Nullable String notFoundPath
	) {
		return getTargetSync(sender, targetUuid.toString(), notFoundPath);
	}

	@Nullable T getOrFindTargetSync(
		@NotNull CommandSender sender,
		@NotNull String targetId,
		@Nullable String notFoundPath
	);

	default @Nullable T getOrFindTargetSync(
		@NotNull CommandSender sender,
		@NotNull UUID targetUuid,
		@Nullable String notFoundPath
	) {
		return getOrFindTargetSync(sender, targetUuid.toString(), notFoundPath);
	}

	@NotNull CompletableFuture<@Nullable T> getTarget(
		@NotNull CommandSender sender,
		@NotNull String targetId,
		@Nullable String notFoundPath
	);

	default @NotNull CompletableFuture<@Nullable T> getTarget(
		@NotNull CommandSender sender,
		@NotNull UUID targetUuid,
		@Nullable String notFoundPath
	) {
		return getTarget(sender, targetUuid.toString(), notFoundPath);
	}

	@NotNull CompletableFuture<@Nullable T> getOrFindTarget(
		@NotNull CommandSender sender,
		@NotNull String targetId,
		@Nullable String notFoundPath
	);

	default @NotNull CompletableFuture<@Nullable T> getOrFindTarget(
		@NotNull CommandSender sender,
		@NotNull UUID targetUuid,
		@Nullable String notFoundPath
	) {
		return getOrFindTarget(sender, targetUuid.toString(), notFoundPath);
	}

	@Nullable T resolveTargetAndGetSync(
		@NotNull CommandSender sender,
		@NotNull String targetName,
		@Nullable final String notFoundPath
	);

	@NotNull CompletableFuture<@Nullable T> resolveTargetAndGet(
		@NotNull CommandSender sender,
		@NotNull String targetName,
		@Nullable final String notFoundPath
	);

	@Nullable T resolveTargetAndGetOrFindSync(
		@NotNull CommandSender sender,
		@NotNull String targetName,
		@Nullable final String notFoundPath
	);

	@NotNull CompletableFuture<@Nullable T> resolveTargetAndGetOrFind(
		@NotNull CommandSender sender,
		@NotNull String targetName,
		@Nullable final String notFoundPath
	);
}
