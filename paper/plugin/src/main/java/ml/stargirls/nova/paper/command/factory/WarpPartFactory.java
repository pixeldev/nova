package ml.stargirls.nova.paper.command.factory;

import ml.stargirls.command.annotated.part.PartFactory;
import ml.stargirls.command.part.CommandPart;
import ml.stargirls.nova.paper.command.part.WarpPart;
import ml.stargirls.nova.paper.warp.Warp;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;

public class WarpPartFactory
	implements PartFactory {

	@Inject private CachedRemoteModelService<Warp> modelService;

	@Override
	public CommandPart createPart(
		@NotNull final String name,
		@NotNull final List<? extends Annotation> list
	) {
		return new WarpPart(name, modelService);
	}
}
