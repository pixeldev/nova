package ml.stargirls.nova.paper.player.property;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerPropertiesHandlerImpl
	implements PlayerPropertiesHandler {

	@Inject private MessageHandler messageHandler;
	@Inject private Logger logger;
	@Inject private PlayerModelService<PlayerPropertiesModel> modelService;
	@Inject private PlayerPropertiesService playerPropertiesService;

	@Override
	public @NotNull CompletableFuture<@NotNull Boolean> toggleProperty(
		@NotNull final Player sender,
		@NotNull final String key
	) {
		UUID uuid = sender.getUniqueId();
		String playerId = uuid.toString();

		return
			modelService.getOrFind(playerId)
				.thenApply(playerPropertiesModel -> {
					if (playerPropertiesModel == null) {
						playerPropertiesModel = playerPropertiesService.createModelWithDefaultProperties(uuid);
					}

					boolean result = playerPropertiesModel.toggleProperty(key);
					modelService.saveSync(playerPropertiesModel);
					return result;
				})
				.whenComplete((result, throwable) -> {
					if (throwable != null) {
						logger.error("Error while toggling property", throwable);
						return;
					}

					Component keyDescription = messageHandler.get(sender, "property.description." + key);
					messageHandler.sendReplacingIn(
						sender,
						SendingModes.PING,
						"property.toggle-" + result,
						Placeholder.component("property", keyDescription));
				});
	}

	@Override
	public @NotNull CompletableFuture<@NotNull Boolean> hasProperty(
		@NotNull final String uuid,
		@NotNull final String key
	) {
		return
			modelService.getOrFind(uuid)
				.handle((playerPropertiesModel, throwable) -> {
					if (throwable != null) {
						logger.error("Error while checking property", throwable);
						return false;
					}

					return hasProperty(playerPropertiesModel, key);
				});
	}

	@Override
	public boolean hasPropertySync(@NotNull final String uuid, @NotNull final String key) {
		return hasProperty(modelService.getOrFindSync(uuid), key);
	}

	private boolean hasProperty(
		@Nullable final PlayerPropertiesModel propertiesModel,
		@NotNull final String key
	) {
		if (propertiesModel == null) {
			return playerPropertiesService.isDefaultProperty(key);
		}

		return propertiesModel.hasProperty(key);
	}
}
