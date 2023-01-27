package ml.stargirls.nova.paper.chat;

import ml.stargirls.maia.paper.notifier.MessageNotifier;
import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.nova.paper.chat.channel.ChatChannel;
import ml.stargirls.nova.paper.chat.channel.ChatChannelService;
import ml.stargirls.nova.paper.chat.mention.MentionManager;
import ml.stargirls.nova.paper.concurrent.ErrorHandler;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class ChatManager {

	@Inject private ChatChannelService channelService;
	@Inject private PlayerModelService<PlayerDisplayIdentityModel> displayIdentityService;
	@Inject private BadWordChecker badWordChecker;
	@Inject private ChatChannel globalChatChannel;
	@Inject private MessageNotifier messageNotifier;
	@Inject private MentionManager mentionManager;
	@Inject private ErrorHandler errorHandler;

	public void sendMessage(@NotNull final Player player, @NotNull final Component message) {
		String rawMessage = PlainTextComponentSerializer.plainText()
			                    .serialize(message);

		badWordChecker.containsBadWords(player, rawMessage, true)
			.thenAccept(badWords -> {
				if (badWords) {
					return;
				}

				PlayerDisplayIdentityModel displayIdentity =
					displayIdentityService.getOrFindSelfSync(player, null);

				if (displayIdentity == null) {
					return;
				}

				char firstChar = rawMessage.charAt(0);
				String finalRawMessage = rawMessage;

				ChatChannel channel = channelService.getByChar(firstChar);
				Component finalMessage = message;

				if (channel == null) {
					channel = channelService.getCurrentChannel(player.getUniqueId());

					if (channel == null) {
						channel = globalChatChannel;
					}
				} else {
					finalRawMessage = rawMessage.substring(1);
					finalMessage = Component.text(finalRawMessage);
				}

				TextColor chatColor = channel.getMessagesColor();

				if (chatColor == null) {
					chatColor = displayIdentity.getChatColor();
				}

				finalMessage = finalMessage.color(chatColor);

				ChatMessageContext messageContext = new ChatMessageContext(
					player,
					displayIdentity,
					finalRawMessage,
					finalMessage);

				Notification chatMessage = channel.generateMessage(messageContext);

				if (chatMessage == null) {
					messageNotifier.sendIn(player, SendingModes.ERROR, "chat.error");
					return;
				}

				messageNotifier.sendReplacing(
					Bukkit.getConsoleSender(),
					chatMessage.path(),
					chatMessage.tagResolvers());
				messageNotifier.sendNotification(chatMessage);
				mentionManager.detectAndNotify(rawMessage, displayIdentity);
			})
			.whenComplete(
				(result, throwable) ->
					errorHandler.checkError(player, "sending chat message", result, throwable));
	}
}
