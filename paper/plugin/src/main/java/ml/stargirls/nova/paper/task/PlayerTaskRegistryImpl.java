package ml.stargirls.nova.paper.task;

import ml.stargirls.maia.paper.concurrent.PluginExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerTaskRegistryImpl
	implements PlayerTaskRegistry {

	@Inject private PluginExecutor pluginExecutor;

	private final Map<UUID, Task> tasks = new HashMap<>();

	@Override
	public void register(@NotNull final Player player, @NotNull final Task task) {
		int bukkitId = pluginExecutor.runLater(
			() -> {
				Consumer<Player> successAction = task.getSuccessAction();
				successAction.accept(player);
				unregister(player);
			},
			task.getDelay()
		);

		task.setBukkitId(bukkitId);
		tasks.put(player.getUniqueId(), task);
	}

	@Override
	public @Nullable Task unregister(@NotNull final UUID playerId) {
		Task task = tasks.remove(playerId);

		if (task == null) {
			return null;
		}

		Bukkit.getScheduler()
			.cancelTask(task.getBukkitId());
		return task;
	}

	@Override
	public @Nullable Task getTask(@NotNull final UUID playerId) {
		return tasks.get(playerId);
	}
}
