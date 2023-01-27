package ml.stargirls.nova.paper.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Task {

	private int bukkitId;

	private final Consumer<Player> successAction;
	private final Consumer<Player> failureAction;

	private final long delay;

	private Task(
		@NotNull final Consumer<Player> successAction,
		@Nullable final Consumer<Player> failureAction,
		final long delay
	) {
		this.successAction = successAction;
		this.failureAction = failureAction;
		this.delay = delay;
	}

	public int getBukkitId() {
		return bukkitId;
	}

	public void setBukkitId(final int bukkitId) {
		this.bukkitId = bukkitId;
	}

	public @NotNull Consumer<Player> getSuccessAction() {
		return successAction;
	}

	public @Nullable Consumer<Player> getFailureAction() {
		return failureAction;
	}

	public long getDelay() {
		return delay;
	}

	@Contract(pure = true, value = "_ -> new")
	public static Builder builder(final long delayInSeconds) {
		return new Builder(delayInSeconds * 20);
	}

	public static class Builder {

		private final long delay;
		private Consumer<Player> successAction;
		private Consumer<Player> failureAction;

		private Builder(final long delay) {
			this.delay = delay;
		}

		@Contract(pure = true, value = "_ -> this")
		public Builder success(@NotNull final Consumer<Player> successAction) {
			this.successAction = successAction;
			return this;
		}

		@Contract(pure = true, value = "_ -> this")
		public Builder failure(@Nullable final Consumer<Player> failureAction) {
			this.failureAction = failureAction;
			return this;
		}

		public Task build() {
			return new Task(
				successAction,
				failureAction,
				delay
			);
		}
	}
}
