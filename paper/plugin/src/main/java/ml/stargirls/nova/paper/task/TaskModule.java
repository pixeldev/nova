package ml.stargirls.nova.paper.task;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class TaskModule
	extends ProtectedModule {

	@Override
	protected void configure() {
		bind(PlayerTaskRegistry.class)
			.to(PlayerTaskRegistryImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerTaskRegistry.class);
	}
}
