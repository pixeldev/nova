package ml.stargirls.nova.paper.concurrent;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.inject.Inject;

public class ErrorHandlerImpl
	implements ErrorHandler {

	@Inject private Logger logger;
	@Inject private MessageHandler messageHandler;

	@Override
	public <T> @Nullable T checkError(
		@NotNull final CommandSender sender,
		@NotNull final String processName,
		@Nullable final T value,
		@Nullable final Throwable error
	) {
		if (error != null) {
			messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
			logger.error("An error has occurred while executing process '" + processName + "'", error);
			return null;
		}

		// we shouldn't handle it here
		// since it was already handled
		return value;
	}

	@Override
	public boolean checkError(
		@NotNull final CommandSender sender,
		@NotNull final String processName,
		@Nullable final Throwable error
	) {
		if (error != null) {
			messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
			logger.error("An error has occurred while executing process '" + processName + "'", error);
			return true;
		}

		return false;
	}
}
