package ml.stargirls.nova.paper.player.connection;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;

public interface ConnectionContext
	extends Cancellable {

	@NotNull Player getPlayer();

	@NotNull String getPlayerIdAsString();

	@NotNull Queue<@NotNull SyncRunnable> getSyncTasks();

	void addSyncTask(@NotNull SyncRunnable runnable);
}
