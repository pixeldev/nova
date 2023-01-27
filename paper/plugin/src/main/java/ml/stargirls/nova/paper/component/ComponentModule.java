package ml.stargirls.nova.paper.component;

import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;

public class ComponentModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(ComponentMatcher.class).to(ComponentMatcherImpl.class)
			.in(Scopes.SINGLETON);

		expose(ComponentMatcher.class);
	}
}
