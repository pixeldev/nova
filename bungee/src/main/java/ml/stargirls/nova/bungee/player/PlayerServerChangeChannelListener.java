package ml.stargirls.nova.bungee.player;

import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.storage.redis.channel.RedisChannel;
import ml.stargirls.storage.redis.channel.RedisChannelListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

public class PlayerServerChangeChannelListener
	implements RedisChannelListener<ServerChangeRequest> {

	@Inject private PlayerServerChangeRegistry registry;

	@Override
	public void listen(
		@NotNull RedisChannel<ServerChangeRequest> channel,
		@NotNull String server,
		ServerChangeRequest object
	) {
		if (!object.approved()) {
			return;
		}

		String destination = object.destination();
		ServerInfo destinationServer = ProxyServer
			                               .getInstance()
			                               .getServerInfo(destination);

		if (destinationServer == null) {
			return;
		}

		UUID playerId = object.playerId();
		registry.add(object);

		ProxiedPlayer player = ProxyServer
			                       .getInstance()
			                       .getPlayer(playerId);

		if (player == null) {
			return;
		}

		player.connect(destinationServer);
	}
}
