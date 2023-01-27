package ml.stargirls.nova.paper.player.home;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.concurrent.ErrorHandler;
import ml.stargirls.nova.paper.home.HomeHandler;
import ml.stargirls.nova.paper.home.HomeModel;
import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.*;

public class PlayerHomeHandler {

	@Inject private MessageHandler messageHandler;
	@Inject private ErrorHandler errorHandler;
	@Inject private HomeHandler homeHandler;
	@Inject private PlayerHomeConfigurationHandler playerHomeConfigurationHandler;
	@Inject private PlayerModelService<PlayerHomeModel> homeModelService;

	public void teleportToHome(@NotNull final Player sender, @NotNull final String id) {
		homeModelService.getOrFindSelf(sender, "home.no-homes")
			.thenAccept(playerHomeModel -> {
				if (playerHomeModel == null) {
					return;
				}

				Set<String> homeNames = playerHomeModel.getHomeNames();
				Set<String> sharedHomeNames = playerHomeModel.getSharedHomeNames();

				if (homeNames.isEmpty() && sharedHomeNames.isEmpty()) {
					messageHandler.send(sender, "home.no-homes");
					return;
				}

				String homeId = playerHomeModel.resolveHomeId(id);

				if (homeId == null) {
					messageHandler.sendReplacingIn(
						sender,
						SendingModes.ERROR,
						"home.no-home",
						Placeholder.component(
							"homes",
							playerHomeModel.generateAllHomeNames(Component.text(", "))));
					return;
				}

				homeHandler.teleportToHome(sender, homeId);
			})
			.whenComplete((result, throwable) -> errorHandler.checkError(
				sender,
				"user home teleport",
				throwable));
	}

	public void deleteHome(@NotNull final Player sender, @NotNull final String id) {
		homeModelService.getOrFindSelf(sender, "home.no-homes")
			.thenAccept(playerHomeModel -> {
				if (playerHomeModel == null) {
					return;
				}

				Set<String> homeNames = playerHomeModel.getHomeNames();
				Map<String, PlayerHomeModel.Shared> sharedHomes = playerHomeModel.getSharedHomes();

				if (homeNames.isEmpty() && sharedHomes.isEmpty()) {
					messageHandler.send(sender, "home.no-homes");
					return;
				}

				String homeId = id.toLowerCase(Locale.ROOT);

				if (!homeNames.remove(homeId)) {
					PlayerHomeModel.Shared shared = sharedHomes.remove(homeId);

					if (shared != null) {
						homeModelService.saveSync(playerHomeModel);
						homeHandler.removeSharing(sender, shared.realId(), sender.getUniqueId());
						return;
					}

					messageHandler.sendReplacingIn(
						sender,
						SendingModes.ERROR,
						"home.no-home",
						Placeholder.component(
							"homes",
							playerHomeModel.generateAllHomeNames(Component.text(", "))));
					return;
				}

				homeModelService.saveSync(playerHomeModel);
				HomeModel homeModel = homeHandler.deleteHomeSync(playerHomeModel.formatHomeId(homeId));

				if (homeModel == null) {
					messageHandler.sendIn(sender, SendingModes.ERROR, "home.not-found");
					return;
				}

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"home.deleted",
					Placeholder.component("home", homeModel.getDisplayName()));
			})
			.whenComplete((result, throwable) -> errorHandler.checkError(
				sender,
				"user home delete",
				throwable));
	}

	public void setHome(@NotNull final Player sender, @NotNull final String id) {
		if (playerHomeConfigurationHandler.isInvalidName(id)) {
			messageHandler.sendIn(sender, SendingModes.ERROR, "home.invalid-name");
			return;
		}

		UUID uuid = sender.getUniqueId();
		String uuidString = uuid.toString();
		homeModelService.getOrFind(uuidString)
			.thenApply(playerHomeModel -> {
				String homeName = id.toLowerCase(Locale.ROOT);

				if (playerHomeModel == null) {
					int maximumHomes = playerHomeConfigurationHandler.getDefaultLimit(sender);
					int maximumSharedHomes =
						playerHomeConfigurationHandler.getDefaultSharedLimit(sender);

					playerHomeModel = new PlayerHomeModel(
						uuid,
						new HashSet<>(1),
						new HashMap<>(),
						maximumHomes,
						maximumSharedHomes);
				} else {
					Set<String> homeNames = playerHomeModel.getHomeNames();
					int currentHomes = homeNames.size();

					if (currentHomes >= playerHomeModel.getMaxHomes()) {
						messageHandler.sendIn(sender, SendingModes.ERROR, "home.limit");
						return null;
					}

					if (homeNames.contains(homeName)) {
						messageHandler.sendIn(sender, SendingModes.ERROR, "home.already-exists");
						return null;
					}
				}

				homeHandler.createHomeSync(
					homeName,
					uuid,
					LocationModel.centered(sender.getLocation()));

				playerHomeModel.addHome(homeName);
				homeModelService.saveSync(playerHomeModel);
				return playerHomeModel;
			})
			.whenComplete((playerHomeModel, throwable) -> {
				if (errorHandler.checkError(sender, "user home set", playerHomeModel, throwable) == null) {
					return;
				}

				int remainingHomes = playerHomeModel.getMaxHomes() - playerHomeModel.getHomeNames()
					                                                     .size();

				messageHandler.sendReplacingIn(
					sender,
					SendingModes.PING,
					"home.set-success",
					Placeholder.component("remaining", Component.text(remainingHomes)));
			});
	}
}
