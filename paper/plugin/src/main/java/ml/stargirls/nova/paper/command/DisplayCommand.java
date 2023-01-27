package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.annotated.annotation.OptArg;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityHandler;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class DisplayCommand
	implements CommandClass {

	@Inject private PlayerDisplayIdentityHandler identityHandler;

	@Command(names = "nick", permission = "nick")
	public void nick(@Sender Player sender, @OptArg String nick) {
		identityHandler.changeDisplayName(sender, nick);
	}

	@Command(names = "color", permission = "color")
	public void color(@Sender Player sender, TextColor color) {
		identityHandler.changeChatColor(sender, color);
	}
}
