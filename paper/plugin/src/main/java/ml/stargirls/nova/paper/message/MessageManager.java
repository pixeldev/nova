package ml.stargirls.nova.paper.message;

import ml.stargirls.maia.paper.notifier.MessageNotifier;
import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.nova.paper.chat.BadWordChecker;
import ml.stargirls.nova.paper.concurrent.ErrorHandler;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import ml.stargirls.nova.paper.player.ignore.PlayerIgnoreChecker;
import ml.stargirls.nova.paper.player.message.PlayerRecentMessageModel;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import ml.stargirls.nova.paper.player.property.NovaPlayerProperties;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesHandler;
import ml.stargirls.storage.dist.RemoteModelService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

public class MessageManager {

	@Inject private RemoteModelService<PlayerRecentMessageModel> recentMessageModelService;
	@Inject private PlayerPropertiesHandler playerPropertiesHandler;

	@Inject private BadWordChecker badWordChecker;
	@Inject private PlayerModelService<PlayerDisplayIdentityModel> displayIdentityModelService;
	@Inject private PlayerIgnoreChecker playerIgnoreChecker;

	@Inject private MessageNotifier messageNotifier;
	@Inject private ErrorHandler errorHandler;

	public void sendMessage(
		@NotNull final Player sender,
		@NotNull final String targetName,
		@NotNull final String message
	) {
		if (targetName.equalsIgnoreCase(sender.getName())) {
			messageNotifier.sendIn(sender, SendingModes.ERROR, "message.self");
			return;
		}

		badWordChecker.containsBadWords(sender, message, true)
			.thenAccept(badWords -> {
				if (badWords) {
					return;
				}

				PlayerDisplayIdentityModel targetDisplayIdentity =
					displayIdentityModelService.resolveTargetAndGetSync(sender, targetName, null);

				if (targetDisplayIdentity == null) {
					return;
				}

				if (cannotSendMessage(sender, targetDisplayIdentity)) {
					return;
				}

				sendMessage(sender, targetDisplayIdentity, message);
			})
			.whenComplete(
				(result, throwable) ->
					errorHandler.checkError(sender, "replying message", result, throwable));
	}

	public void reply(@NotNull final Player sender, @NotNull final String message) {
		UUID senderUuid = sender.getUniqueId();
		String senderUuidString = senderUuid.toString();

		badWordChecker.containsBadWords(sender, message, true)
			.thenAccept(badWords -> {
				if (badWords) {
					return;
				}

				PlayerRecentMessageModel recentMessageModel =
					recentMessageModelService.findSync(senderUuidString);

				if (recentMessageModel == null) {
					messageNotifier.sendIn(sender, SendingModes.ERROR, "message.no-recent-message");
					return;
				}

				PlayerDisplayIdentityModel targetDisplayIdentity =
					displayIdentityModelService.getTargetSync(
						sender,
						recentMessageModel.lastSentPlayerId(),
						null);

				if (targetDisplayIdentity == null) {
					return;
				}

				if (cannotSendMessage(sender, targetDisplayIdentity)) {
					return;
				}

				sendMessage(sender, targetDisplayIdentity, message);
			})
			.whenComplete(
				(result, throwable) ->
					errorHandler.checkError(sender, "replying message", result, throwable));
	}

	private void sendMessage(
		@NotNull final Player sender,
		@NotNull final PlayerDisplayIdentityModel targetDisplayIdentity,
		@NotNull final String message
	) {
		PlayerDisplayIdentityModel senderDisplayIdentity =
			displayIdentityModelService.getOrFindSelfSync(sender, null);

		if (senderDisplayIdentity == null) {
			return;
		}

		UUID targetId = targetDisplayIdentity.getPlayerId();
		UUID senderId = sender.getUniqueId();

		PlayerRecentMessageModel messageData = new PlayerRecentMessageModel(targetId, senderId);
		recentMessageModelService.saveSync(messageData);

		TagResolver messageResolver = Placeholder.parsed("message", message);
		TagResolver senderResolver =
			Placeholder.component("sender", senderDisplayIdentity.getFullDisplayName());
		TagResolver targetResolver =
			Placeholder.component("target", targetDisplayIdentity.getFullDisplayName());

		messageNotifier.sendReplacing(sender, "message.sender-format", targetResolver,
		                              messageResolver);

		messageNotifier.sendNotification(Notification.personalIn(
			targetId,
			SendingModes.PING,
			"message.target-format",
			senderResolver,
			messageResolver));

		messageNotifier.sendNotification(Notification.globalFiltering(
			"message.social-spy-format",
			SocialSpyMessageNotifierFilter.ID,
			senderResolver,
			targetResolver,
			messageResolver));
	}

	private boolean cannotSendMessage(
		@NotNull final Player sender,
		@NotNull final PlayerDisplayIdentityModel targetIdentityModel
	) {
		String senderUuid = sender.getUniqueId()
			                    .toString();

		if (!playerPropertiesHandler.hasPropertySync(senderUuid, NovaPlayerProperties.MESSAGES)) {
			messageNotifier.sendIn(sender, SendingModes.ERROR, "message.self-not-receiving");
			return true;
		}

		if (cannotReceiveMessage(sender, targetIdentityModel)) {
			return true;
		}

		if (playerIgnoreChecker.isIgnoredSync(senderUuid, targetIdentityModel.getPlayerId())) {
			messageNotifier.sendIn(
				sender,
				SendingModes.ERROR,
				"message.self-ignoring-target",
				Placeholder.component("player", targetIdentityModel.getFullDisplayName()));
			return true;
		}

		return cannotReceiveMessage(sender, targetIdentityModel);
	}

	private boolean cannotReceiveMessage(
		@NotNull final Player sender,
		@NotNull final PlayerDisplayIdentityModel targetIdentityModel
	) {
		String targetUuid = targetIdentityModel.getId();

		if (!playerPropertiesHandler.hasPropertySync(targetUuid, NovaPlayerProperties.MESSAGES)) {
			messageNotifier.sendIn(sender, SendingModes.ERROR, "message.target-not-receiving");
			return true;
		}

		if (playerIgnoreChecker.isIgnoredSync(targetUuid, sender.getUniqueId())) {
			messageNotifier.sendIn(sender, SendingModes.ERROR, "message.target-ignoring-sender");
			return true;
		}

		return false;
	}
}
