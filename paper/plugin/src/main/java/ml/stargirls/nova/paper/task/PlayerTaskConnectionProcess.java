package ml.stargirls.nova.paper.task;

import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerTaskConnectionProcess
	implements ConnectionProcess {

	@Inject private PlayerTaskRegistry taskRegistry;

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) {
		taskRegistry.unregister(context.getPlayer());
	}

	@Override
	public void processSwitchServer(
		@NotNull final ConnectionContext context,
		@NotNull final String destination
	) {
		taskRegistry.unregister(context.getPlayer());
	}
}
