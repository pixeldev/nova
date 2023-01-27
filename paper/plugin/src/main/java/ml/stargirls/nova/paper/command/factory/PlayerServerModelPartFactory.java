package ml.stargirls.nova.paper.command.factory;

import ml.stargirls.command.annotated.part.PartFactory;
import ml.stargirls.command.part.CommandPart;
import ml.stargirls.nova.paper.command.part.PlayerServerModelPart;
import ml.stargirls.nova.paper.player.server.PlayerServerModel;
import ml.stargirls.storage.dist.RemoteModelService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;

public class PlayerServerModelPartFactory
	implements PartFactory {

	@Inject private RemoteModelService<PlayerServerModel> modelService;

	@Override
	public CommandPart createPart(
		@NotNull final String name,
		@NotNull final List<? extends Annotation> list
	) {
		return new PlayerServerModelPart(name, modelService);
	}
}
