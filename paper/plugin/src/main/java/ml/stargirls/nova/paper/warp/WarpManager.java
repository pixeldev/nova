package ml.stargirls.nova.paper.warp;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.location.ServerLocationModel;
import ml.stargirls.nova.paper.player.permission.PermissionHelper;
import ml.stargirls.nova.paper.player.teleport.PlayerTeleportHandler;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;

public class WarpManager {

	@Inject private CachedRemoteModelService<Warp> modelService;
	@Inject private PlayerTeleportHandler teleportHandler;

	@Inject private Logger logger;
	@Inject private MessageHandler messageHandler;

	private final String actualServer;

	@Inject
	public WarpManager(@NotNull final ServerInfo serverInfo) {
		this.actualServer = serverInfo.getId();
	}

	public void createWarp(
		@NotNull final Player sender,
		@NotNull final String id,
		final boolean listed
	) {
		modelService.getOrFind(id)
			.thenApply(warp -> {
				if (warp != null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.already-exists");
					return null;
				}

				warp = new Warp(
					id,
					false,
					listed,
					Component.text(id),
					new ServerLocationModel(
						actualServer,
						LocationModel.centered(sender.getLocation())));

				modelService.saveSync(warp);
				return warp;
			})
			.whenComplete((warp, throwable) -> {
				if (throwable != null) {
					logger.error("Error while creating warp", throwable);
					messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
					return;
				}

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"warp.created",
					Placeholder.component("warp", warp.getDisplayName()));
			});
	}

	public void toggleRestricted(@NotNull final CommandSender sender, @NotNull final String id) {
		modelService.getOrFind(id)
			.thenApply(warp -> {
				if (warp == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.not-found");
					return null;
				}

				boolean restricted = !warp.isRestricted();
				warp.setRestricted(restricted);
				modelService.saveSync(warp);
				return warp;
			})
			.whenComplete((warp, throwable) -> {
				if (throwable != null) {
					logger.error("Error while setting warp permission", throwable);
					messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
					return;
				}

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"warp.restricted-" + warp.isRestricted(),
					Placeholder.component("warp", warp.getDisplayName()));
			});
	}

	public void toggleListed(@NotNull final CommandSender sender, @NotNull final String id) {
		modelService.getOrFind(id)
			.thenApply(warp -> {
				if (warp == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.not-found");
					return null;
				}

				boolean hidden = !warp.isListed();
				warp.setListed(hidden);
				modelService.saveSync(warp);
				return warp;
			})
			.whenComplete((warp, throwable) -> {
				if (throwable != null) {
					logger.error("Error while setting warp permission", throwable);
					messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
					return;
				}

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"warp.listed-" + warp.isListed(),
					Placeholder.component("warp", warp.getDisplayName()));
			});
	}

	public void renameWarp(
		@NotNull final CommandSender sender,
		@NotNull final String id,
		@NotNull final Component newName
	) {
		modelService.getOrFind(id)
			.thenApply(warp -> {
				if (warp == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.not-found");
					return null;
				}

				warp.setDisplayName(newName);
				modelService.saveSync(warp);
				return warp;
			})
			.whenComplete((warp, throwable) -> {
				if (throwable != null) {
					logger.error("Error while renaming warp", throwable);
					messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
					return;
				}

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"warp.renamed",
					Placeholder.component("warp", Component.text(warp.getId())),
					Placeholder.component("name", warp.getDisplayName()));
			});
	}

	public void teleportToWarp(@NotNull final Player sender, @NotNull final String id) {
		modelService.getOrFind(id)
			.thenAccept(warp -> {
				if (warp == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.not-found");
					return;
				}

				if (warp.isRestricted() && !PermissionHelper.hasPermission(
					sender,
					"warp." + id,
					"warp.all")) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.restricted");
					return;
				}

				teleportHandler.teleport(sender, warp.getLocation());
			})
			.whenComplete((unused, throwable) -> {
				if (throwable != null) {
					logger.error("Error while teleporting to warp", throwable);
					messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
				}
			});
	}

	public void deleteWarp(@NotNull final CommandSender sender, @NotNull final String id) {
		modelService.delete(id)
			.whenComplete((deleted, throwable) -> {
				if (throwable != null) {
					logger.error("Error while deleting warp " + id, throwable);
					messageHandler.sendIn(sender, SendingModes.ERROR, "process-error");
					return;
				}

				if (!deleted) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "warp.not-found");
					return;
				}

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"warp.deleted",
					Placeholder.component("warp", Component.text(id)));
			});
	}
}
