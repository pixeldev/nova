package ml.stargirls.nova.paper.player.teleport;

import ml.stargirls.nova.paper.location.LocationModel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PendingTeleportModel(
	@NotNull String playerName, @Nullable UUID targetId,
	@Nullable LocationModel locationModel
) {

	@Contract(pure = true, value = "_, _ -> new")
	public static @NotNull PendingTeleportModel target(
		@NotNull final Player player,
		@NotNull final UUID targetId
	) {
		return new PendingTeleportModel(player.getName(), targetId, null);
	}

	@Contract(pure = true, value = "_, _ -> new")
	public static @NotNull PendingTeleportModel location(
		@NotNull final Player player,
		@NotNull final LocationModel locationModel
	) {
		return new PendingTeleportModel(player.getName(), null, locationModel);
	}
}
