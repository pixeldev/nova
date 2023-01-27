package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.ArgOrSub;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.annotated.annotation.Text;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.maia.paper.command.part.AsyncCompletable;
import ml.stargirls.nova.paper.command.factory.PlayerServerModelPartFactory;
import ml.stargirls.nova.paper.message.MessageManager;
import ml.stargirls.nova.paper.player.property.NovaPlayerProperties;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesHandler;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = { "message", "m", "msg" })
@ArgOrSub(true)
public class MessageCommand
	implements CommandClass {

	@Inject private MessageManager messageManager;
	@Inject private PlayerPropertiesHandler playerPropertiesHandler;

	@Command(names = "")
	public void message(
		@Sender Player sender,
		@AsyncCompletable(PlayerServerModelPartFactory.class) String target,
		@Text String message
	) {
		messageManager.sendMessage(sender, target, message);
	}

	@Command(names = { "sp", "socialspy" }, permission = "message.socialspy")
	public void socialSpy(@Sender Player sender) {
		playerPropertiesHandler.toggleProperty(sender, NovaPlayerProperties.SOCIAL_SPY);
	}

	@Command(names = { "toggle", "t" }, permission = "message.toggle")
	public void ignoreMessages(@Sender Player sender) {
		playerPropertiesHandler.toggleProperty(sender, NovaPlayerProperties.MESSAGES);
	}
}
