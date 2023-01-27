package ml.stargirls.nova.paper.player.resolve;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class PlayerTargetResolverImpl
	implements PlayerTargetResolver {

	@Inject private MessageHandler messageHandler;
	@Inject private PlayerResolver playerResolver;

	@Override
	public @Nullable String resolveIdStringSync(
		@NotNull final CommandSender sender,
		@NotNull final String targetName
	) {
		String id = playerResolver.resolveIdStringSync(targetName);

		if (id == null) {
			messageHandler.sendIn(sender, SendingModes.ERROR, "user.target-not-found");
		}

		return id;
	}

	@Override
	public @Nullable UUID resolveIdSync(
		@NotNull final CommandSender sender,
		@NotNull final String targetName
	) {
		UUID id = playerResolver.resolveIdSync(targetName);

		if (id == null) {
			messageHandler.sendIn(sender, SendingModes.ERROR, "user.target-not-found");
		}

		return id;
	}

	@Override
	public @Nullable String resolveNameSync(
		@NotNull final CommandSender sender,
		@NotNull final UUID targetId
	) {
		String name = playerResolver.resolveNameSync(targetId);

		if (name == null) {
			messageHandler.sendIn(sender, SendingModes.ERROR, "user.target-not-found");
		}

		return name;
	}
}
