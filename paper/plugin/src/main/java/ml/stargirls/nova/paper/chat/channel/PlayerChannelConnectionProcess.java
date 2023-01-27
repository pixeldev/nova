package ml.stargirls.nova.paper.chat.channel;

import ml.stargirls.nova.paper.chat.channel.player.PlayerChannelModel;
import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerChannelConnectionProcess
	implements ConnectionProcess {

	@Inject private CachedRemoteModelService<PlayerChannelModel> playerChannelModelService;

	@Override
	public void processConnect(@NotNull final ConnectionContext context) {
		playerChannelModelService.findSync(context.getPlayerIdAsString());
	}

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context) {
		playerChannelModelService.deleteInCache(context.getPlayerIdAsString());
	}
}
