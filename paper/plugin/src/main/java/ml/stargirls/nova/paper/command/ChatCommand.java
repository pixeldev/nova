package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.ArgOrSub;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.nova.paper.chat.channel.ChatChannel;
import ml.stargirls.nova.paper.chat.channel.ChatChannelHandler;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = "chat")
@ArgOrSub(true)
public class ChatCommand
	implements CommandClass {

	@Inject private ChatChannelHandler chatChannelHandler;

	@Command(names = "")
	public void joinChannel(@Sender Player sender, ChatChannel channel) {
		chatChannelHandler.joinToChannel(sender, channel);
	}

	@Command(names = "leave")
	public void leaveChannel(@Sender Player sender) {
		chatChannelHandler.exitFromChannel(sender);
	}
}
