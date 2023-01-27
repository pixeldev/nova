package ml.stargirls.nova.paper.player.server;

import ml.stargirls.storage.model.Model;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerServerModel
	implements Model {

	private final UUID uuid;
	private final String name;
	private String serverId;

	public PlayerServerModel(
		@NotNull final UUID uuid,
		@NotNull final String name,
		@NotNull final String serverId
	) {
		this.uuid = uuid;
		this.name = name;
		this.serverId = serverId;
	}

	@Contract(pure = true, value = "_, _ -> new")
	public static PlayerServerModel create(
		@NotNull final Player player,
		@NotNull final String actualServer
	) {
		return new PlayerServerModel(
			player.getUniqueId(),
			player.getName(),
			actualServer
		);
	}

	@Override
	public @NotNull String getId() {
		return uuid.toString();
	}

	public @NotNull UUID getUuid() {
		return uuid;
	}

	public @NotNull String getName() {
		return name;
	}

	public @NotNull String getServerId() {
		return serverId;
	}

	public void setServer(@NotNull final String serverId) {
		this.serverId = serverId;
	}
}