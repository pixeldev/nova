package ml.stargirls.nova.paper.player.server;

import ml.stargirls.nova.paper.player.resolve.PlayerResolver;
import ml.stargirls.storage.dist.RemoteModelService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerServerServiceImpl
	implements PlayerServerService {

	@Inject private RemoteModelService<PlayerServerModel> modelService;
	@Inject private Executor executor;
	@Inject private Logger logger;
	@Inject private PlayerResolver playerResolver;

	@Override
	public @NotNull CompletableFuture<@Nullable Queue<@Nullable PlayerServerModel>> query(
		final boolean allowDuplicates,
		final boolean requireNonNull,
		@NotNull final String... usernames
	) {
		if (usernames.length == 0) {
			return CompletableFuture.completedFuture(null);
		}

		return
			CompletableFuture
				.supplyAsync(
					() -> {
						Queue<PlayerServerModel> data = new LinkedList<>();
						Collection<UUID> resolvedIds = playerResolver.resolveIdsSync(
							allowDuplicates,
							usernames);

						if (resolvedIds == null || resolvedIds.isEmpty()) {
							return null;
						}

						for (UUID id : resolvedIds) {
							if (id == null) {
								if (requireNonNull) {
									throw new IllegalArgumentException("One of the usernames was not found");
								}

								continue;
							}

							PlayerServerModel playerServerModel = modelService.findSync(id.toString());

							if (playerServerModel == null) {
								if (requireNonNull) {
									throw new IllegalArgumentException("No server data found of player " + id);
								}
							}

							data.add(playerServerModel);
						}

						return data;
					},
					executor
				)
				.whenComplete((result, throwable) -> {
					if (throwable != null) {
						logger.error("Failed to query player server data", throwable);
					}
				});
	}

	@Override
	public @NotNull CompletableFuture<@Nullable PlayerServerModel> find(
		@NotNull final String username
	) {
		String id = playerResolver.resolveIdStringSync(username);

		if (id == null) {
			return CompletableFuture.completedFuture(null);
		}

		return modelService.find(id)
			       .whenComplete((result, throwable) -> {
				       if (throwable != null) {
					       logger.error("Failed to find player server data", throwable);
				       }
			       });
	}

	@Override
	public @Nullable PlayerServerModel findSync(@NotNull final String username) {
		String id = playerResolver.resolveIdStringSync(username);

		if (id == null) {
			return null;
		}

		return modelService.findSync(id);
	}
}
