package ml.stargirls.nova.paper.command.part;

import ml.stargirls.command.CommandContext;
import ml.stargirls.command.stack.ArgumentStack;
import ml.stargirls.maia.paper.command.CommandHelper;
import ml.stargirls.maia.paper.command.part.AsyncCompletablePart;
import ml.stargirls.nova.paper.player.permission.PermissionHelper;
import ml.stargirls.nova.paper.warp.Warp;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class WarpPart
	extends AsyncCompletablePart {

	private final CachedRemoteModelService<Warp> modelService;

	public WarpPart(
		@NotNull final String name,
		@NotNull final CachedRemoteModelService<Warp> modelService
	) {
		super(name);
		this.modelService = modelService;
	}

	@Override
	public List<String> getSuggestions(
		@NotNull final CommandContext commandContext,
		@NotNull final ArgumentStack stack
	) {
		Collection<Warp> warps = modelService.getAllSync();

		if (warps == null) {
			return Collections.emptyList();
		}

		String argument = CommandHelper.extractLastArg(stack);

		if (argument == null) {
			return warps.stream()
				       .map(Warp::getId)
				       .toList();
		}

		CommandSender sender = CommandHelper.extractSender(commandContext);

		return warps.stream()
			       .filter(warp -> {
				       if (!warp.isListed()) {
					       return false;
				       }

				       String id = warp.getId(); // it's lowercase

				       if (!warp.isRestricted()) {
					       return id.startsWith(argument.toLowerCase(Locale.ROOT));
				       }

				       return PermissionHelper.hasPermission(sender, "warp." + id, "warp.all") &&
				              id.startsWith(argument.toLowerCase(Locale.ROOT));
			       })
			       .map(Warp::getId)
			       .toList();
	}
}
