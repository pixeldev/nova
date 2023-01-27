package ml.stargirls.nova.paper.chat.channel;

import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.nova.paper.chat.ChatMessageContext;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlobalChatChannel
	implements ChatChannel {

	@Override
	public @NotNull String getId() {
		return "global";
	}

	@Override
	public char getPrefix() {
		return NO_PREFIX;
	}

	@Override
	public @Nullable TextColor getMessagesColor() {
		return null;
	}

	@Override
	public Notification generateMessage(@NotNull final ChatMessageContext context) {
		return Notification.global(
			"chat.channel.global.format",
			Placeholder.component("player",
			                      context.displayIdentity()
				                      .getFullDisplayName()),
			Placeholder.component("message", context.message())
		);
	}
}
