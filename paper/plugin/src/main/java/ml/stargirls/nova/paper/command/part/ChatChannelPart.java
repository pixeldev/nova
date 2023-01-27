package ml.stargirls.nova.paper.command.part;

import ml.stargirls.command.CommandContext;
import ml.stargirls.command.exception.ArgumentParseException;
import ml.stargirls.command.part.ArgumentPart;
import ml.stargirls.command.part.CommandPart;
import ml.stargirls.command.stack.ArgumentStack;
import ml.stargirls.maia.paper.command.CommandHelper;
import ml.stargirls.nova.paper.chat.channel.ChatChannel;
import ml.stargirls.nova.paper.chat.channel.ChatChannelService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ChatChannelPart
	implements ArgumentPart {

	private final String name;
	private final ChatChannelService channelService;

	public ChatChannelPart(
		@NotNull final String name,
		@NotNull final ChatChannelService channelService
	) {
		this.name = name;
		this.channelService = channelService;
	}

	@Override
	public List<?> parseValue(
		@NotNull final CommandContext commandContext,
		@NotNull final ArgumentStack argumentStack,
		@NotNull final CommandPart commandPart
	) throws ArgumentParseException {
		String channelName = argumentStack.next()
			                     .toLowerCase(Locale.ROOT);
		ChatChannel channel = channelService.getById(channelName);

		if (channel == null) {
			throw new ArgumentParseException(Component.translatable("unknown.channel"));
		}

		return Collections.singletonList(channel);
	}

	@Override
	public List<String> getSuggestions(
		@NotNull final CommandContext commandContext,
		@NotNull final ArgumentStack stack
	) {
		String argument = CommandHelper.extractLastArg(stack);

		if (argument == null) {
			return null;
		}

		String channelName = argument.toLowerCase(Locale.ROOT);

		return channelService.getChannelIds()
			       .stream()
			       .filter(id -> id.startsWith(channelName))
			       .toList();
	}

	@Override
	public String getName() {
		return name;
	}
}
