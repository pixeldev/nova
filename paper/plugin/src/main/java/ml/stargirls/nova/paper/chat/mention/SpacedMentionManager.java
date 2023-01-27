package ml.stargirls.nova.paper.chat.mention;

import ml.stargirls.maia.paper.notifier.MessageNotifier;
import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import ml.stargirls.nova.paper.player.property.NovaPlayerProperties;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesHandler;
import ml.stargirls.nova.paper.player.resolve.PlayerResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

public class SpacedMentionManager
	implements MentionManager {

	@Inject private MessageNotifier messageNotifier;
	@Inject private PlayerResolver playerResolver;
	@Inject private PlayerPropertiesHandler playerPropertiesHandler;

	@Override
	public @Nullable Collection<UUID> getMentionedPlayers(@NotNull final String message) {
		String[] words = message.split(" ");
		return playerResolver.resolveIdsSync(false, words);
	}

	@Override
	public void detectAndNotify(
		@NotNull final String message,
		@NotNull final PlayerDisplayIdentityModel senderDisplayIdentity
	) {
		Collection<UUID> mentionedPlayers = getMentionedPlayers(message);

		if (mentionedPlayers == null) {
			return;
		}

		mentionedPlayers =
			mentionedPlayers.stream()
				.filter(uuid -> playerPropertiesHandler.hasPropertySync(
					uuid,
					NovaPlayerProperties.MENTIONS))
				.toList();

		messageNotifier.sendNotification(Notification.targetsIn(
			mentionedPlayers,
			SendingModes.PING,
			"chat.mention",
			Placeholder.component("player", senderDisplayIdentity.getFullDisplayName())));
	}
}
