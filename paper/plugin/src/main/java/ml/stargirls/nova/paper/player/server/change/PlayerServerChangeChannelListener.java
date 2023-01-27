package ml.stargirls.nova.paper.player.server.change;

import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.nova.paper.player.connection.ConnectionProcessManager;
import ml.stargirls.storage.redis.channel.RedisChannel;
import ml.stargirls.storage.redis.channel.RedisChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

public class PlayerServerChangeChannelListener
	implements RedisChannelListener<ServerChangeRequest> {

	@Inject private ConnectionProcessManager processManager;

	@Override
	public void listen(
		@NotNull final RedisChannel<ServerChangeRequest> channel,
		@NotNull final String server,
		@NotNull final ServerChangeRequest object
	) {
		if (object.approved()) {
			return;
		}

		UUID playerId = object.playerId();
		Player player = Bukkit.getPlayer(playerId);

		if (player == null) {
			// this should never happen since velocity
			// will send message to the disconnecting server
			return;
		}

		processManager.handleSwitch(player, object.destination());
	}
}
