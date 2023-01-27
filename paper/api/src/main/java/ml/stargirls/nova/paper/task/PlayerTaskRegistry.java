package ml.stargirls.nova.paper.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerTaskRegistry {

	void register(@NotNull Player player, Task task);

	@Nullable Task unregister(@NotNull UUID playerId);

	default @Nullable Task unregister(@NotNull Player player) {
		return unregister(player.getUniqueId());
	}

	@Nullable Task getTask(@NotNull UUID playerId);

	default @Nullable Task getTask(@NotNull Player player) {
		return getTask(player.getUniqueId());
	}
}
