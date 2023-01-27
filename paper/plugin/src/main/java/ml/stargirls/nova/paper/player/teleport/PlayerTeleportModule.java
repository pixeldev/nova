package ml.stargirls.nova.paper.player.teleport;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import ml.stargirls.maia.inject.ProtectedModule;

public class PlayerTeleportModule
	extends ProtectedModule {

	@Override
	public void configure() {
		Key<PlayerTeleportHandler> key = Key.get(PlayerTeleportHandler.class, Names.named("direct"));

		bind(key)
			.to(PlayerTeleportHandlerImpl.class)
			.in(Scopes.SINGLETON);
		expose(key);

		bind(PlayerTeleportHandler.class)
			.to(DelayedPlayerTeleportHandler.class)
			.in(Scopes.SINGLETON);
		bind(PlayerTeleportManager.class).in(Scopes.SINGLETON);
	}
}
