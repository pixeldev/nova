package ml.stargirls.nova.paper.player.property;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerPropertiesHandler {

	@NotNull CompletableFuture<@NotNull Boolean> toggleProperty(
		@NotNull Player sender,
		@NotNull String key
	);

	default @NotNull CompletableFuture<@NotNull Boolean> hasProperty(
		@NotNull Player sender,
		@NotNull String key
	) {
		return hasProperty(sender.getUniqueId(), key);
	}

	default @NotNull CompletableFuture<@NotNull Boolean> hasProperty(
		@NotNull UUID uuid,
		@NotNull String key
	) {
		return hasProperty(uuid.toString(), key);
	}

	@NotNull CompletableFuture<@NotNull Boolean> hasProperty(
		@NotNull String uuid,
		@NotNull String key
	);

	default boolean hasPropertySync(@NotNull Player sender, @NotNull String key) {
		return hasPropertySync(sender.getUniqueId(), key);
	}

	default boolean hasPropertySync(@NotNull UUID uuid, @NotNull String key) {
		return hasPropertySync(uuid.toString(), key);
	}

	boolean hasPropertySync(@NotNull String uuid, @NotNull String key);
}
