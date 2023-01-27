package ml.stargirls.nova.paper.concurrent;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class ConcurrentModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(ErrorHandler.class)
			.to(ErrorHandlerImpl.class)
			.in(Scopes.SINGLETON);
		expose(ErrorHandler.class);
	}
}
