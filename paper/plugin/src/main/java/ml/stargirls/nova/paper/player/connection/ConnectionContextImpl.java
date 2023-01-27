package ml.stargirls.nova.paper.player.connection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

public class ConnectionContextImpl
	implements ConnectionContext {

	private final Player player;
	private final String playerId;
	private final Queue<SyncRunnable> syncTasks;
	private boolean cancelled;

	protected ConnectionContextImpl(@NotNull final Player player) {
		this.player = player;
		this.playerId = player.getUniqueId()
			                .toString();
		this.syncTasks = new LinkedList<>();
	}

	@Override
	public @NotNull Player getPlayer() {
		return player;
	}

	@Override
	public @NotNull String getPlayerIdAsString() {
		return playerId;
	}

	@Override
	public @NotNull Queue<SyncRunnable> getSyncTasks() {
		return syncTasks;
	}

	@Override
	public void addSyncTask(@NotNull final SyncRunnable runnable) {
		syncTasks.add(runnable);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean cancel) {
		cancelled = cancel;
	}
}
