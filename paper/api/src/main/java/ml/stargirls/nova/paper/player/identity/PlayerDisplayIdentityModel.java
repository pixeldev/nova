package ml.stargirls.nova.paper.player.identity;

import ml.stargirls.storage.model.Model;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerDisplayIdentityModel
	implements Model {

	private final UUID playerId;
	private Component displayName;
	private Component prefix;
	private TextColor chatColor;

	public PlayerDisplayIdentityModel(
		@NotNull final UUID playerId,
		@NotNull final Component displayName,
		@NotNull final Component prefix,
		@Nullable final TextColor chatColor
	) {
		this.playerId = playerId;
		this.displayName = displayName;
		this.prefix = prefix;
		this.chatColor = chatColor;
	}

	public static PlayerDisplayIdentityModel create(
		@NotNull final Player player,
		@NotNull final Component prefix
	) {
		return new PlayerDisplayIdentityModel(player.getUniqueId(), player.displayName(), prefix,
		                                      null);
	}

	@Override
	public @NotNull String getId() {
		return playerId.toString();
	}

	public @NotNull UUID getPlayerId() {
		return playerId;
	}

	public @NotNull Component getDisplayName() {
		return displayName;
	}

	public void setDisplayName(@NotNull final Component displayName) {
		this.displayName = displayName;
	}

	public @NotNull Component getPrefix() {
		return prefix;
	}

	public void setPrefix(@NotNull final Component prefix) {
		this.prefix = prefix;
	}

	public @Nullable TextColor getChatColor() {
		return chatColor;
	}

	public void setChatColor(@Nullable final TextColor chatColor) {
		this.chatColor = chatColor;
	}

	public @NotNull Component getFullDisplayName() {
		return prefix.append(displayName);
	}
}
