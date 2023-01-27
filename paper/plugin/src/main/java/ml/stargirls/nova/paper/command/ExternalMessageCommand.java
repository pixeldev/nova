package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.annotated.annotation.Text;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.nova.paper.message.MessageManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class ExternalMessageCommand
	implements CommandClass {

	@Inject private MessageManager messageManager;

	@Command(names = { "reply", "r" })
	public void reply(@Sender Player sender, @Text String message) {
		messageManager.reply(sender, message);
	}
}
