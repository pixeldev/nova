package ml.stargirls.nova.paper.player;

import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.nova.paper.player.connection.DisconnectModule;
import ml.stargirls.nova.paper.player.data.PlayerDataModule;
import ml.stargirls.nova.paper.player.home.PlayerHomeModule;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModule;
import ml.stargirls.nova.paper.player.ignore.PlayerIgnoreModule;
import ml.stargirls.nova.paper.player.message.PlayerMessageModule;
import ml.stargirls.nova.paper.player.property.PlayerPropertyModule;
import ml.stargirls.nova.paper.player.resolve.PlayerResolverModule;
import ml.stargirls.nova.paper.player.server.PlayerServerModule;
import ml.stargirls.nova.paper.player.teleport.PlayerTeleportModule;

public class PlayerModule
	extends ProtectedModule {

	@Override
	public void configure() {
		install(new DisconnectModule());
		install(new PlayerServerModule());
		install(new PlayerDataModule());
		install(new PlayerDisplayIdentityModule());
		install(new PlayerResolverModule());
		install(new PlayerTeleportModule());
		install(new PlayerMessageModule());
		install(new PlayerIgnoreModule());
		install(new PlayerPropertyModule());
		install(new PlayerHomeModule());
	}
}
