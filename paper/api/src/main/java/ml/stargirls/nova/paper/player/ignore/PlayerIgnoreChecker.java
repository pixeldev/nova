package ml.stargirls.nova.paper.player.ignore;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerIgnoreChecker {

	boolean isIgnoredSync(@NotNull String playerId, @NotNull UUID targetId);

	default boolean isIgnoredSync(@NotNull final UUID playerId, @NotNull final UUID target) {
		return isIgnoredSync(playerId.toString(), target);
	}

	@NotNull CompletableFuture<@NotNull Boolean> isIgnored(
		@NotNull String playerId,
		@NotNull UUID targetId
	);

	default @NotNull CompletableFuture<@NotNull Boolean> isIgnored(
		@NotNull final UUID playerId,
		@NotNull final UUID target
	) {
		return isIgnored(playerId.toString(), target);
	}

	boolean isIgnoredSync(@NotNull String playerId, @NotNull String targetName);

	default boolean isIgnoredSync(@NotNull final UUID playerId, @NotNull final String targetName) {
		return isIgnoredSync(playerId.toString(), targetName);
	}

	@NotNull CompletableFuture<@NotNull Boolean> isIgnored(
		@NotNull String playerId,
		@NotNull String targetName
	);

	default @NotNull CompletableFuture<@NotNull Boolean> isIgnored(
		@NotNull final UUID playerId,
		@NotNull final String targetName
	) {
		return isIgnored(playerId.toString(), targetName);
	}
}
