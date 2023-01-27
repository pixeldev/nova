package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.nova.paper.player.property.NovaPlayerProperties;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesHandler;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class MentionCommand
	implements CommandClass {

	@Inject private PlayerPropertiesHandler playerPropertiesHandler;

	@Command(names = "mentions", permission = "mentions.toggle")
	public void toggle(@Sender Player sender) {
		playerPropertiesHandler.toggleProperty(sender, NovaPlayerProperties.MENTIONS);
	}
}
