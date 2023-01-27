package ml.stargirls.nova.paper.player.teleport;

import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.location.ServerLocationModel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerTeleportHandler {

	void teleport(@NotNull Player player, @NotNull Player target);

	void teleport(@NotNull Player player, @NotNull Location location);

	void teleport(@NotNull Player player, @NotNull LocationModel locationModel);

	void teleport(@NotNull Player player, @NotNull ServerLocationModel locationModel);

	void teleport(@NotNull Player player, @NotNull UUID targetId);
}
