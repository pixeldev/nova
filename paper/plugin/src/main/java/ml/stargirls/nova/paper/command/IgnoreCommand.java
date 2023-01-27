package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.ArgOrSub;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.maia.paper.command.part.AsyncCompletable;
import ml.stargirls.nova.paper.command.factory.PlayerServerModelPartFactory;
import ml.stargirls.nova.paper.player.ignore.PlayerIgnoreHandler;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = "ignore")
@ArgOrSub(true)
public class IgnoreCommand
	implements CommandClass {

	@Inject private PlayerIgnoreHandler playerIgnoreHandler;

	@Command(names = "")
	public void main(
		@Sender Player sender,
		@AsyncCompletable(PlayerServerModelPartFactory.class) String target
	) {
		playerIgnoreHandler.addIgnore(sender, target);
	}

	@Command(names = "remove")
	public void remove(
		@Sender Player sender,
		@AsyncCompletable(PlayerServerModelPartFactory.class) String target
	) {
		playerIgnoreHandler.removeIgnore(sender, target);
	}
}
