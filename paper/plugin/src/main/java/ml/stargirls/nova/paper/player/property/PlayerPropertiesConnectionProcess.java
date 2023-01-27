package ml.stargirls.nova.paper.player.property;

import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerPropertiesConnectionProcess
	implements ConnectionProcess {

	@Inject private PlayerModelService<PlayerPropertiesModel> modelService;

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) {
		modelService.deleteInCache(context.getPlayerIdAsString());
	}
}
