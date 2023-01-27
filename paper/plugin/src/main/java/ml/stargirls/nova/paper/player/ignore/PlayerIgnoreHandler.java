package ml.stargirls.nova.paper.player.ignore;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.concurrent.ErrorHandler;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerIgnoreHandler {

	@Inject private MessageHandler messageHandler;
	@Inject private ErrorHandler errorHandler;

	@Inject private Executor executor;
	@Inject private CachedRemoteModelService<PlayerIgnoreModel> modelService;

	@Inject private PlayerModelService<PlayerDisplayIdentityModel> displayIdentityModelService;

	public void addIgnore(
		@NotNull final Player player,
		@NotNull final String targetName
	) {
		if (targetName.equalsIgnoreCase(player.getName())) {
			messageHandler.sendIn(player, SendingModes.ERROR, "user.cannot-self");
			CompletableFuture.completedFuture(false);
			return;
		}

		CompletableFuture
			.supplyAsync(
				() -> {
					PlayerDisplayIdentityModel targetIdentity =
						displayIdentityModelService.resolveTargetAndGetSync(player, targetName, null);

					if (targetIdentity == null) {
						return null;
					}

					UUID playerId = player.getUniqueId();
					UUID targetId = targetIdentity.getPlayerId();

					PlayerIgnoreModel playerIgnoreModel =
						modelService.getOrFindSync(playerId.toString());

					if (playerIgnoreModel == null) {
						playerIgnoreModel = new PlayerIgnoreModel(playerId, Set.of(targetId));
					} else {
						if (!playerIgnoreModel.addIgnored(targetId)) {
							messageHandler.sendIn(player, SendingModes.ERROR, "ignore.already-ignored");
							return null;
						}
					}

					modelService.saveSync(playerIgnoreModel);
					return targetIdentity;
				},
				executor
			)
			.handle((targetDisplayIdentity, throwable) -> {
				if (errorHandler.checkError(
					player,
					"ignoring player",
					targetDisplayIdentity,
					throwable) == null) {
					return false;
				}

				messageHandler.sendReplacingIn(
					player,
					SendingModes.PING,
					"ignore.ignore-true",
					Placeholder.component("player", targetDisplayIdentity.getFullDisplayName()));
				return true;
			});
	}

	public void removeIgnore(
		@NotNull final Player player,
		@NotNull final String targetName
	) {
		if (targetName.equalsIgnoreCase(player.getName())) {
			messageHandler.sendIn(player, SendingModes.ERROR, "user.cannot-self");
			CompletableFuture.completedFuture(false);
			return;
		}

		CompletableFuture
			.supplyAsync(
				() -> {
					PlayerDisplayIdentityModel targetIdentity =
						displayIdentityModelService.resolveTargetAndGetSync(player, targetName, null);

					if (targetIdentity == null) {
						return null;
					}

					PlayerIgnoreModel playerIgnoreModel =
						modelService.getOrFindSync(player.getUniqueId()
							                           .toString());

					if (playerIgnoreModel == null ||
					    !playerIgnoreModel.removeIgnored(targetIdentity.getPlayerId())) {
						messageHandler.sendIn(player, SendingModes.ERROR, "ignore.not-ignored");
						return null;
					}

					if (playerIgnoreModel.hasIgnoredPlayers()) {
						modelService.saveSync(playerIgnoreModel);
					} else {
						modelService.deleteSync(playerIgnoreModel);
					}

					return targetIdentity;
				},
				executor
			)
			.handle((targetDisplayIdentity, throwable) -> {
				if (errorHandler.checkError(
					player,
					"remove ignoring player",
					targetDisplayIdentity,
					throwable
				) == null) {
					return false;
				}

				messageHandler.sendReplacingIn(
					player,
					SendingModes.PING,
					"ignore.ignore-false",
					Placeholder.component("player", targetDisplayIdentity.getFullDisplayName()));
				return true;
			});
	}
}
