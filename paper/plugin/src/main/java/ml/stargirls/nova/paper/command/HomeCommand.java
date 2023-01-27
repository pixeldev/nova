package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.nova.paper.player.home.PlayerHomeHandler;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class HomeCommand
	implements CommandClass {

	@Inject private PlayerHomeHandler playerHomeHandler;

	@Command(names = "sethome")
	public void setHome(@Sender Player sender, String name) {
		playerHomeHandler.setHome(sender, name);
	}

	@Command(names = "delhome")
	public void delHome(@Sender Player sender, String name) {
		playerHomeHandler.deleteHome(sender, name);
	}

	@Command(names = "home")
	public void home(@Sender Player sender, String name) {
		playerHomeHandler.teleportToHome(sender, name);
	}
}
