package ml.stargirls.nova.paper.luckperms;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class LuckPermsModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(LuckPermsHandler.class).to(LuckPermsHandlerImpl.class).in(Scopes.SINGLETON);
	}
}
