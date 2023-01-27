package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.nova.paper.warp.WarpManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Command(names = "spawn")
public class SpawnCommand
	implements CommandClass {

	@Inject private WarpManager warpManager;

	@Command(names = "")
	public void spawn(@Sender Player sender) {
		warpManager.teleportToWarp(sender, "spawn");
	}

	@Command(names = "set", permission = "spawn.set")
	public void set(@Sender Player sender) {
		warpManager.createWarp(sender, "spawn", false);
	}

	@Command(names = "unset", permission = "spawn.unset")
	public void unset(@Sender Player sender) {
		warpManager.deleteWarp(sender, "spawn");
	}
}
