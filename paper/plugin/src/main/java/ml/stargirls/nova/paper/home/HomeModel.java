package ml.stargirls.nova.paper.home;

import ml.stargirls.nova.paper.location.ServerLocationModel;
import ml.stargirls.storage.model.Model;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class HomeModel
	implements Model {

	public static final String ID_FORMAT = "%s:%s";

	private final String name;
	private final UUID ownerId;
	private final Date creation;
	private final Map<UUID, Shared> sharingPlayers;
	private boolean enabled;
	private Date lastUse;
	private Component displayName;
	private ServerLocationModel location;

	public HomeModel(
		@NotNull final String name,
		@NotNull final UUID ownerId,
		@NotNull final Date creation,
		@NotNull final Map<UUID, Shared> sharingPlayers,
		final boolean enabled,
		@NotNull final Date lastUse,
		@NotNull final Component displayName,
		@NotNull final ServerLocationModel location
	) {
		this.name = name;
		this.ownerId = ownerId;
		this.creation = creation;
		this.sharingPlayers = sharingPlayers;
		this.enabled = enabled;
		this.lastUse = lastUse;
		this.displayName = displayName;
		this.location = location;
	}

	@Override
	public @NotNull String getId() {
		return ID_FORMAT.formatted(ownerId.toString(), name);
	}

	public @NotNull String getName() {
		return name;
	}

	public @NotNull UUID getOwnerId() {
		return ownerId;
	}

	public @NotNull Date getCreation() {
		return creation;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public @NotNull Date getLastUse() {
		return lastUse;
	}

	public @Nullable Date getLastUse(@NotNull final UUID playerId) {
		Shared shared = sharingPlayers.get(playerId);
		return shared == null ? null : shared.getLastUse();
	}

	public void setLastUse(@NotNull final Date lastUse) {
		this.lastUse = lastUse;
	}

	public void setLastUse(@NotNull final UUID playerId, @NotNull final Date lastUse) {
		Shared shared = sharingPlayers.get(playerId);
		if (shared != null) {
			shared.setLastUse(lastUse);
		}
	}

	protected @NotNull Map<UUID, Shared> getSharingPlayers() {
		return sharingPlayers;
	}

	public boolean removeSharing(@NotNull final UUID playerId) {
		return sharingPlayers.remove(playerId) != null;
	}

	public boolean isPlayerSharing(@NotNull final UUID playerId) {
		return sharingPlayers.containsKey(playerId);
	}

	public @NotNull Component getDisplayName() {
		return displayName;
	}

	public void setDisplayName(@NotNull final Component displayName) {
		this.displayName = displayName;
	}

	public @NotNull ServerLocationModel getLocation() {
		return location;
	}

	public void setLocation(@NotNull final ServerLocationModel location) {
		this.location = location;
	}

	public static class Shared {

		private final UUID playerId;
		private Date lastUse;

		public Shared(@NotNull final UUID playerId, @NotNull final Date lastUse) {
			this.playerId = playerId;
			this.lastUse = lastUse;
		}

		public @NotNull UUID getPlayerId() {
			return playerId;
		}

		public @NotNull Date getLastUse() {
			return lastUse;
		}

		public void setLastUse(@NotNull final Date lastUse) {
			this.lastUse = lastUse;
		}
	}
}
