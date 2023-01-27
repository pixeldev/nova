package ml.stargirls.nova.paper.player.server;

import ml.stargirls.nova.paper.player.connection.ConnectionProcessManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerServerSenderImpl
	implements PlayerServerSender {

	@Inject private ConnectionProcessManager connectionProcessManager;

	@Override
	public boolean sendToServerSync(@NotNull final Player player, @NotNull final String destination)
		throws Exception {
		connectionProcessManager.handleSwitchSync(player, destination);
		return true;
	}
}
