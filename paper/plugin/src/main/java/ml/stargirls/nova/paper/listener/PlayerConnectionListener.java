package ml.stargirls.nova.paper.listener;

import ml.stargirls.nova.paper.player.connection.ConnectionProcessManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerConnectionListener
	implements Listener {

	@Inject private ConnectionProcessManager connectionProcessManager;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.joinMessage(null);
		connectionProcessManager.handleConnect(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(@NotNull final PlayerQuitEvent event) {
		event.quitMessage(null);
		connectionProcessManager.handleDisconnect(event.getPlayer());
	}
}
