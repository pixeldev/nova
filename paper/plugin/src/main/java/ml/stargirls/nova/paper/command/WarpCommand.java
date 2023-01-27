package ml.stargirls.nova.paper.command;

import ml.stargirls.command.annotated.CommandClass;
import ml.stargirls.command.annotated.annotation.ArgOrSub;
import ml.stargirls.command.annotated.annotation.Command;
import ml.stargirls.command.annotated.annotation.Switch;
import ml.stargirls.command.bukkit.annotation.Sender;
import ml.stargirls.maia.paper.command.part.AsyncCompletable;
import ml.stargirls.nova.paper.command.factory.WarpPartFactory;
import ml.stargirls.nova.paper.warp.WarpManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Locale;

@Command(names = "warp")
@ArgOrSub(true)
public class WarpCommand
	implements CommandClass {

	@Inject private WarpManager warpManager;

	@Command(names = "")
	public void teleport(
		@Sender Player sender,
		@AsyncCompletable(WarpPartFactory.class) String warp
	) {
		warpManager.teleportToWarp(sender, warp.toLowerCase(Locale.ROOT));
	}

	@Command(names = "listed", permission = "warp.listed")
	public void listed(@Sender Player sender, @AsyncCompletable(WarpPartFactory.class) String warp) {
		warpManager.toggleListed(sender, warp.toLowerCase(Locale.ROOT));
	}

	@Command(names = "rename", permission = "warp.rename")
	public void rename(
		CommandSender sender,
		@AsyncCompletable(WarpPartFactory.class) String warp,
		Component newName
	) {
		warpManager.renameWarp(sender, warp.toLowerCase(Locale.ROOT), newName);
	}

	@Command(names = "create", permission = "warp.create")
	public void create(@Sender Player sender, String warp, @Switch("l") boolean listed) {
		warpManager.createWarp(sender, warp.toLowerCase(Locale.ROOT), listed);
	}

	@Command(names = "remove", permission = "warp.delete")
	public void delete(CommandSender sender, @AsyncCompletable(WarpPartFactory.class) String warp) {
		warpManager.deleteWarp(sender, warp.toLowerCase(Locale.ROOT));
	}

	@Command(names = "restricted", permission = "warp.restricted")
	public void restricted(
		CommandSender sender,
		@AsyncCompletable(WarpPartFactory.class) String warp
	) {
		warpManager.toggleRestricted(sender, warp.toLowerCase(Locale.ROOT));
	}
}
