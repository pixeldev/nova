package ml.stargirls.nova.paper.message;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class MessageModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(MessageManager.class).in(Scopes.SINGLETON);
	}
}
