package ml.stargirls.nova.paper.player.identity;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.chat.BadWordChecker;
import ml.stargirls.nova.paper.component.ComponentMatcher;
import ml.stargirls.nova.paper.concurrent.ErrorHandler;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class PlayerDisplayIdentityHandler {

	@Inject private Configuration configuration;
	@Inject private PlayerModelService<PlayerDisplayIdentityModel> displayIdentityModelService;
	@Inject private BadWordChecker badWordChecker;

	@Inject private ErrorHandler errorHandler;
	@Inject private MessageHandler messageHandler;
	@Inject private ComponentMatcher componentMatcher;

	public void changeChatColor(@NotNull final Player player, @NotNull final TextColor color) {
		displayIdentityModelService.getOrFindSelf(player, null)
			.thenAccept(displayIdentityModel -> {
				if (displayIdentityModel == null) {
					return;
				}

				displayIdentityModel.setChatColor(color);
				displayIdentityModelService.saveSync(displayIdentityModel);
			})
			.whenComplete((ignored, throwable) -> {
				if (errorHandler.checkError(player, "changing chat color", throwable)) {
					return;
				}

				Component demo = messageHandler.get(player, "display.color.demo")
					                 .color(color);

				messageHandler.sendReplacingIn(
					player,
					SendingModes.PING,
					"display.color.success",
					Placeholder.component("demo", demo));
			});
	}

	public void changeDisplayName(
		@NotNull final Player player,
		@Nullable final String displayName
	) {
		TextComponent displayNameComponent;

		if (displayName == null) {
			displayNameComponent = Component.text(player.getName());
		} else {
			displayNameComponent = LegacyComponentSerializer.legacyAmpersand()
				                       .deserialize(displayName);

			String displayNamePlain = componentMatcher.match(displayNameComponent, player.getName());

			if (displayNamePlain == null) {
				messageHandler.sendIn(player, SendingModes.ERROR, "display.name.only-change-decoration");
				return;
			}

			int displayNameLength = displayNamePlain.length();

			if (displayNameLength > configuration.getDisplay()
				                        .getMaxNickLength()) {
				messageHandler.sendIn(player, SendingModes.ERROR, "display.name.too-long");
				return;
			}

			if (displayNameLength < configuration.getDisplay()
				                        .getMinNickLength()) {
				messageHandler.sendIn(player, SendingModes.ERROR, "display.name.too-short");
				return;
			}

			if (configuration.getDisplay()
				    .getInvalidPattern()
				    .matcher(displayNamePlain)
				    .find()) {
				messageHandler.sendIn(player, SendingModes.ERROR, "display.name.invalid");
				return;
			}
		}

		badWordChecker.containsBadWords(
				player,
				PlainTextComponentSerializer.plainText()
					.serialize(displayNameComponent),
				true)
			.thenAccept(badWords -> {
				if (badWords) {
					return;
				}

				PlayerDisplayIdentityModel displayIdentityModel =
					displayIdentityModelService.getOrFindSync(player.getUniqueId()
						                                          .toString());

				if (displayIdentityModel == null) {
					messageHandler.sendIn(player, SendingModes.ERROR, "user.self-not-found");
					return;
				}

				displayIdentityModel.setDisplayName(displayNameComponent);
				player.displayName(displayNameComponent);
				displayIdentityModelService.saveSync(displayIdentityModel);
			})
			.whenComplete((ignored, throwable) -> {
				if (errorHandler.checkError(player, "changing chat color", throwable)) {
					return;
				}

				messageHandler.sendReplacingIn(
					player,
					SendingModes.PING,
					"display.name.success",
					Placeholder.component("name", displayNameComponent));
			});
	}
}
