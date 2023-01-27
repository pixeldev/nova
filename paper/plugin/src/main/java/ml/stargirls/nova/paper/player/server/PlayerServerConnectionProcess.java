package ml.stargirls.nova.paper.player.server;

import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import ml.stargirls.storage.dist.RemoteModelService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerServerConnectionProcess
	implements ConnectionProcess {

	@Inject private RemoteModelService<PlayerServerModel> serverDataService;

	private final String actualServer;

	@Inject
	public PlayerServerConnectionProcess(@NotNull final ServerInfo serverInfo) {
		this.actualServer = serverInfo.getId();
	}

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) {
		serverDataService.deleteSync(context.getPlayerIdAsString());
	}

	@Override
	public void processConnect(@NotNull final ConnectionContext context) {
		Player player = context.getPlayer();
		PlayerServerModel serverData = serverDataService.findSync(player.getUniqueId()
			                                                          .toString());

		if (serverData == null) {
			serverData = PlayerServerModel.create(player, actualServer);
		} else {
			serverData.setServer(actualServer);
		}

		serverDataService.saveSync(serverData);
	}
}
