package ml.stargirls.nova.paper.message;

import ml.stargirls.maia.paper.notifier.filter.MessageNotifierFilter;
import ml.stargirls.nova.paper.player.property.NovaPlayerProperties;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class SocialSpyMessageNotifierFilter
	implements MessageNotifierFilter {

	public static final String ID = "social-spy";

	@Inject private PlayerPropertiesHandler playerPropertiesHandler;

	@Override
	public @NotNull String getId() {
		return ID;
	}

	@Override
	public boolean isDenied(@NotNull final Player player) {
		return playerPropertiesHandler.hasPropertySync(player, NovaPlayerProperties.SOCIAL_SPY);
	}
}
