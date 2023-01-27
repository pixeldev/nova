package ml.stargirls.nova.paper.chat.channel;

import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.nova.paper.chat.ChatMessageContext;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChatChannel {

	char NO_PREFIX = Character.MIN_VALUE;

	@NotNull String getId();

	char getPrefix();

	/**
	 * @return the color of the channel's messages or null if the channel doesn't have a color, in
	 * 	which case the message will be sent with the
	 *  {@link PlayerDisplayIdentityModel#getChatColor()}
	 * 	color.
	 */
	@Nullable TextColor getMessagesColor();

	/**
	 * NOTE: This method is called asynchronously.
	 *
	 * @param context
	 * 	the context of the message
	 *
	 * @return the message to send
	 */
	@Nullable Notification generateMessage(@NotNull ChatMessageContext context);
}
