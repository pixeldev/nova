package ml.stargirls.nova.paper.player.resolve;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class PlayerResolverModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerResolver.class).to(PlayerResolverImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerResolver.class);

		bind(PlayerTargetResolver.class).to(PlayerTargetResolverImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerTargetResolver.class);

		bind(PlayerRegistry.class).to(RedisPlayerRegistry.class)
			.in(Scopes.SINGLETON);
		expose(PlayerRegistry.class);

		bind(PlayerNameSanitizer.class).to(PlayerNameSanitizerImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerNameSanitizer.class);
	}
}
