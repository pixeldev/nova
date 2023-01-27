package ml.stargirls.nova.paper.player.connection;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class DisconnectModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(ConnectionProcessRegistry.class)
			.to(ConnectionProcessRegistryImpl.class)
			.in(Scopes.SINGLETON);
		bind(ConnectionProcessManager.class).in(Scopes.SINGLETON);
	}
}
