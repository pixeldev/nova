package ml.stargirls.nova.bungee.listener;

import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.nova.bungee.player.PlayerServerChangeRegistry;
import ml.stargirls.storage.dist.RemoteModelService;
import ml.stargirls.storage.redis.channel.RedisChannel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.inject.Inject;
import java.util.UUID;

public class PlayerPreConnectListener
	implements Listener {

	@Inject private PlayerServerChangeRegistry changeRegistry;
	@Inject private RedisChannel<ServerChangeRequest> channel;
	@Inject private RemoteModelService<ServerInfo> serverInfoModelService;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreConnect(ServerConnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		Server previousServer = player.getServer();

		if (previousServer == null) {
			return;
		}

		UUID playerId = event
			                .getPlayer()
			                .getUniqueId();

		if (changeRegistry.remove(playerId) == null) {
			ServerInfo serverInfo = serverInfoModelService
				                        .findSync(previousServer
					                                  .getInfo()
					                                  .getName());

			if (serverInfo == null) {
				return;
			}

			String destination = event
				                     .getTarget()
				                     .getName();
			ServerChangeRequest changeRequest =
				new ServerChangeRequest(
					playerId,
					destination,
					false
				);

			channel.sendMessage(
				changeRequest,
				serverInfo.getId()
			);

			event.setCancelled(true);
		}
	}
}
