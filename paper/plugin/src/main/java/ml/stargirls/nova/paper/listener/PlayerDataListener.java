package ml.stargirls.nova.paper.listener;

import ml.stargirls.nova.paper.player.data.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerDataListener
	implements Listener {

	@Inject private PlayerDataManager playerDataManager;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldSave(@NotNull final WorldSaveEvent event) {
		playerDataManager.saveAllInventories();
	}
}
