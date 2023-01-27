package ml.stargirls.nova.paper.player.ignore;

import ml.stargirls.storage.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class PlayerIgnoreModel
	implements Model {

	private final UUID uuid;
	private final Set<UUID> ignoredPlayers;

	protected PlayerIgnoreModel(
		final UUID uuid,
		final Set<UUID> ignoredPlayers
	) {
		this.uuid = uuid;
		this.ignoredPlayers = ignoredPlayers;
	}

	public @NotNull UUID getUuid() {
		return uuid;
	}

	protected @NotNull Set<UUID> getIgnoredPlayers() {
		return ignoredPlayers;
	}

	public boolean hasIgnoredPlayers() {
		return !ignoredPlayers.isEmpty();
	}

	public boolean isIgnored(final UUID uuid) {
		return ignoredPlayers.contains(uuid);
	}

	public boolean addIgnored(final UUID uuid) {
		return ignoredPlayers.add(uuid);
	}

	public boolean removeIgnored(final UUID uuid) {
		return ignoredPlayers.remove(uuid);
	}

	@Override
	public @NotNull String getId() {
		return uuid.toString();
	}
}
