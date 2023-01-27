package ml.stargirls.nova.paper.command.factory;

import ml.stargirls.command.annotated.part.Key;
import ml.stargirls.command.part.CommandPart;
import ml.stargirls.maia.paper.command.factory.InjectablePartFactory;
import ml.stargirls.nova.paper.chat.channel.ChatChannel;
import ml.stargirls.nova.paper.chat.channel.ChatChannelService;
import ml.stargirls.nova.paper.command.part.ChatChannelPart;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;

public class ChatChannelPartFactory
	implements InjectablePartFactory {

	@Inject private ChatChannelService channelService;

	@Override
	public Key getKey() {
		return new Key(ChatChannel.class);
	}

	@Override
	public CommandPart createPart(
		@NotNull final String name,
		@NotNull final List<? extends Annotation> list
	) {
		return new ChatChannelPart(name, channelService);
	}
}
