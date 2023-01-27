package ml.stargirls.nova.paper.command.part;

import ml.stargirls.command.CommandContext;
import ml.stargirls.command.stack.ArgumentStack;
import ml.stargirls.maia.paper.command.CommandHelper;
import ml.stargirls.maia.paper.command.part.AsyncCompletablePart;
import ml.stargirls.nova.paper.player.server.PlayerServerModel;
import ml.stargirls.storage.dist.RemoteModelService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class PlayerServerModelPart
	extends AsyncCompletablePart {

	private final RemoteModelService<PlayerServerModel> modelService;

	public PlayerServerModelPart(
		@NotNull final String name,
		@NotNull final RemoteModelService<PlayerServerModel> modelService
	) {
		super(name);
		this.modelService = modelService;
	}

	@Override
	public List<String> getSuggestions(
		@NotNull final CommandContext commandContext,
		@NotNull final ArgumentStack stack
	) {
		String argument = CommandHelper.extractLastArg(stack);

		if (argument == null) {
			return null;
		}

		List<PlayerServerModel> models = modelService.findAllSync();

		if (models == null) {
			return null;
		}

		String playerName = argument.toLowerCase(Locale.ROOT);

		return models.parallelStream()
			       .map(PlayerServerModel::getName)
			       .filter(name -> name.toLowerCase(Locale.ROOT)
				                       .startsWith(playerName))
			       .toList();
	}
}
