package ml.stargirls.nova.paper.player.teleport;

import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

public class PlayerTeleportConnectionProcess
	implements ConnectionProcess {

	@Inject private PlayerTeleportManager teleportManager;

	@Inject
	@Named("direct")
	private PlayerTeleportHandler teleportHandler;

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) {
		teleportManager.deletePendingTeleport(context.getPlayer()
			                                      .getName());
	}

	@Override
	public void processConnect(@NotNull final ConnectionContext context) {
		PendingTeleportModel model = teleportManager.invalidatePendingTeleport(context.getPlayer());

		if (model == null) {
			return;
		}

		UUID targetId = model.targetId();
		Player player = context.getPlayer();

		if (targetId != null) {
			teleportHandler.teleport(player, targetId);
			return;
		}

		LocationModel locationModel = model.locationModel();

		if (locationModel != null) {
			teleportHandler.teleport(player, locationModel);
		}
	}
}
