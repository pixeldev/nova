package ml.stargirls.nova.paper.player.data;

import ml.stargirls.storage.dist.CachedRemoteModelService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerDataManager {

	@Inject private CachedRemoteModelService<PlayerDataModel> dataModelService;
	@Inject private CachedRemoteModelService<PlayerInventoryModel> inventoryModelService;
	@Inject private Logger logger;
	@Inject private Executor executor;

	public @NotNull FullPlayerData loadAndSetSync(@NotNull final Player player) {
		String playerId = player.getUniqueId()
			                  .toString();

		PlayerDataModel playerDataModel = dataModelService.getOrFindSync(playerId);
		PlayerInventoryModel playerInventoryModel = inventoryModelService.getOrFindSync(playerId);
		return new FullPlayerData(playerDataModel, playerInventoryModel);
	}

	public void saveInventorySync(@NotNull final Player player) throws IOException {
		PlayerInventoryModel inventoryData = PlayerInventoryModel.create(player);
		inventoryModelService.saveSync(inventoryData);
	}

	public void saveDataSync(@NotNull final Player player) {
		PlayerDataModel playerDataModel = PlayerDataModel.create(player);
		dataModelService.saveSync(playerDataModel);
	}

	public void saveAllSync(@NotNull final Player player) throws IOException {
		saveInventorySync(player);
		saveDataSync(player);
	}

	public void saveAllInventories() {
		CompletableFuture.runAsync(
			() -> Bukkit.getOnlinePlayers()
				      .forEach(player -> {
					      try {
						      saveInventorySync(player);
					      } catch (IOException e) {
						      logger.warn(
							      "Failed to save inventory data for player " + player.getUniqueId(),
							      e);
					      }
				      }),
			executor);
	}
}
