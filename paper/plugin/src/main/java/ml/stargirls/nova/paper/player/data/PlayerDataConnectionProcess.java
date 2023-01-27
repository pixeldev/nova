package ml.stargirls.nova.paper.player.data;

import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;

public class PlayerDataConnectionProcess
	implements ConnectionProcess {

	@Inject private PlayerDataManager playerDataManager;

	@Override
	public void processConnect(@NotNull final ConnectionContext context) {
		Player player = context.getPlayer();

		FullPlayerData loadedData = playerDataManager.loadAndSetSync(player);

		context.addSyncTask(() -> {
			PlayerDataModel playerDataModel = loadedData.playerDataModel();

			if (playerDataModel != null) {
				playerDataModel.apply(player);
			}

			PlayerInventoryModel inventoryData = loadedData.playerInventoryModel();

			if (inventoryData != null) {
				inventoryData.apply(player);
			}
		});
	}

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) throws IOException {
		playerDataManager.saveAllSync(context.getPlayer());
	}

	@Override
	public void processSwitchServer(
		@NotNull final ConnectionContext context,
		@NotNull final String destination
	) throws Exception {
		playerDataManager.saveAllSync(context.getPlayer());
	}
}
