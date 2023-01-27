package ml.stargirls.nova.paper.concurrent;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ErrorHandler {

	<T> @Nullable T checkError(
		@NotNull CommandSender sender,
		@NotNull String processName,
		@Nullable T value,
		@Nullable Throwable error
	);

	boolean checkError(
		@NotNull CommandSender sender,
		@NotNull String processName,
		@Nullable Throwable error
	);
}
