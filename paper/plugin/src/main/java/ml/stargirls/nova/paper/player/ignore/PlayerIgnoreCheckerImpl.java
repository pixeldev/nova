package ml.stargirls.nova.paper.player.ignore;

import ml.stargirls.nova.paper.player.resolve.PlayerResolver;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerIgnoreCheckerImpl
	implements PlayerIgnoreChecker {

	@Inject private PlayerResolver playerResolver;
	@Inject private CachedRemoteModelService<PlayerIgnoreModel> playerIgnoreModelService;
	@Inject private Executor executor;

	@Override
	public boolean isIgnoredSync(@NotNull final String playerId, @NotNull final UUID targetId) {
		PlayerIgnoreModel playerIgnoreModel = playerIgnoreModelService.getOrFindSync(playerId);
		return playerIgnoreModel != null && playerIgnoreModel.isIgnored(targetId);
	}

	@Override
	public @NotNull CompletableFuture<@NotNull Boolean> isIgnored(
		@NotNull final String playerId,
		@NotNull final UUID targetId
	) {
		return CompletableFuture.supplyAsync(() -> isIgnoredSync(playerId, targetId), executor);
	}

	@Override
	public boolean isIgnoredSync(@NotNull final String playerId, @NotNull final String targetName) {
		UUID targetId = playerResolver.resolveIdSync(targetName);

		if (targetId == null) {
			return false;
		}

		return isIgnoredSync(playerId, targetId);
	}

	@Override
	public @NotNull CompletableFuture<@NotNull Boolean> isIgnored(
		@NotNull final String playerId,
		@NotNull final String targetName
	) {
		return CompletableFuture.supplyAsync(() -> isIgnoredSync(playerId, targetName), executor);
	}
}
