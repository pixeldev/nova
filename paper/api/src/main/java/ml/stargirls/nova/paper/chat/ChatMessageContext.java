package ml.stargirls.nova.paper.chat;

import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ChatMessageContext(
	@NotNull Player player,
	@NotNull PlayerDisplayIdentityModel displayIdentity,
	@NotNull String rawMessage,
	@NotNull Component message
) {
}
