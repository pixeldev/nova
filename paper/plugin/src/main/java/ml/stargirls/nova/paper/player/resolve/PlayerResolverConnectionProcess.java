package ml.stargirls.nova.paper.player.resolve;

import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerResolverConnectionProcess
	implements ConnectionProcess {

	@Inject private PlayerRegistry playerRegistry;

	@Override
	public void processConnect(@NotNull final ConnectionContext context) {
		playerRegistry.registerSync(context.getPlayer());
	}

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) {
		playerRegistry.unregisterSync(
			context.getPlayerIdAsString(),
			context.getPlayer()
				.getName());
	}
}
