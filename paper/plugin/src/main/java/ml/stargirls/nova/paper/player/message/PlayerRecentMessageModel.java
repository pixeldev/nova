package ml.stargirls.nova.paper.player.message;

import ml.stargirls.storage.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PlayerRecentMessageModel(
	@NotNull UUID playerId,
	@NotNull UUID lastSentPlayerId
)
	implements Model {

	@Override
	public @NotNull String getId() {
		return playerId.toString();
	}
}
