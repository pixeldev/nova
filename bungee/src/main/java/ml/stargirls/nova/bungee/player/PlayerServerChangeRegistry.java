package ml.stargirls.nova.bungee.player;

import ml.stargirls.maia.server.request.ServerChangeRequest;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerServerChangeRegistry {

	private final Map<UUID, ServerChangeRequest> registry = new ConcurrentHashMap<>();

	public void add(ServerChangeRequest changeRequest) {
		registry.put(
			changeRequest.playerId(),
			changeRequest
		);
	}

	public @Nullable ServerChangeRequest remove(UUID playerId) {
		return registry.remove(playerId);
	}
}
