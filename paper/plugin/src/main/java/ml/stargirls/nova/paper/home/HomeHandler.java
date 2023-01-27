package ml.stargirls.nova.paper.home;

import ml.stargirls.maia.paper.notifier.MessageNotifier;
import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.concurrent.ErrorHandler;
import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.location.ServerLocationModel;
import ml.stargirls.nova.paper.player.permission.PermissionHelper;
import ml.stargirls.nova.paper.player.teleport.PlayerTeleportHandler;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class HomeHandler {

	@Inject private CachedRemoteModelService<HomeModel> modelService;
	@Inject private PlayerTeleportHandler playerTeleportHandler;
	@Inject private MessageHandler messageHandler;
	@Inject private MessageNotifier messageNotifier;
	@Inject private ErrorHandler errorHandler;

	private final String actualServer;

	@Inject
	public HomeHandler(@NotNull final ServerInfo serverInfo) {
		this.actualServer = serverInfo.getId();
	}

	public @NotNull HomeModel createHomeSync(
		@NotNull final String id,
		@NotNull final UUID ownerId,
		@NotNull final LocationModel locationModel
	) {
		Date now = new Date();
		HomeModel homeModel = new HomeModel(
			id,
			ownerId,
			now,
			new HashMap<>(),
			true,
			now,
			Component.text(id),
			new ServerLocationModel(actualServer, locationModel));

		modelService.saveSync(homeModel);
		return homeModel;
	}

	public @Nullable HomeModel deleteHomeSync(@NotNull final String id) {
		HomeModel homeModel = modelService.findSync(id);

		if (homeModel == null) {
			return null;
		}

		modelService.deleteSync(id);
		return homeModel;
	}

	public void removeSharing(
		@NotNull final Player sender,
		@NotNull final String id,
		@NotNull final UUID playerId
	) {
		modelService.getOrFind(id)
			.thenApply(homeModel -> {
				if (homeModel == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "home.not-found");
					return null;
				}

				if (!homeModel.removeSharing(playerId)) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "home.remove-sharing.not-shared");
					return null;
				}

				modelService.saveSync(homeModel);
				return homeModel;
			})
			.whenComplete((homeModel, throwable) -> {
				if (errorHandler.checkError(
					sender,
					"remove sharing from home",
					homeModel,
					throwable
				) == null) {
					return;
				}

				messageNotifier.sendNotification(Notification.personalIn(
					homeModel.getOwnerId(),
					SendingModes.PING,
					"home.remove-sharing.success-owner",
					Placeholder.component("home", homeModel.getDisplayName()),
					Placeholder.component("player", )));

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"home.remove-sharing.success",
					Placeholder.component("home", homeModel.getDisplayName()));
			});
	}

	public void teleportToHome(@NotNull final Player sender, @NotNull final String homeId) {
		modelService.getOrFind(homeId)
			.thenAccept(homeModel -> {
				if (homeModel == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "home.not-found");
					return;
				}

				if (PermissionHelper.hasPermission(sender, "home.teleport.forced")) {
					playerTeleportHandler.teleport(sender, homeModel.getLocation());
					return;
				}

				if (!homeModel.isEnabled()) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "home.not-enabled");
					return;
				}

				UUID senderUuid = sender.getUniqueId();

				if (homeModel.getOwnerId()
					    .equals(senderUuid)) {
					homeModel.setLastUse(new Date());
				} else {
					if (!homeModel.isPlayerSharing(senderUuid)) {
						messageHandler.sendIn(sender, SendingModes.ERROR, "home.not-shared");
						return;
					}

					homeModel.setLastUse(senderUuid, new Date());
				}

				modelService.saveSync(homeModel);
				playerTeleportHandler.teleport(sender, homeModel.getLocation());
			})
			.whenComplete((result, throwable) -> errorHandler.checkError(
				sender,
				"teleport to home",
				throwable));
	}
}
