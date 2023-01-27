package ml.stargirls.nova.paper.chat.channel.player;

import ml.stargirls.storage.model.Model;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerChannelModel
	implements Model {

	private final UUID uuid;
	private String channelId;

	private PlayerChannelModel(@NotNull final UUID uuid, @Nullable final String channelId) {
		this.uuid = uuid;
		this.channelId = channelId;
	}

	@Contract(pure = true, value = "_ -> new")
	public static PlayerChannelModel create(@NotNull final UUID uuid) {
		return new PlayerChannelModel(uuid, null);
	}

	@Contract(pure = true, value = "_, _ -> new")
	public static PlayerChannelModel create(
		@NotNull final UUID uuid,
		@Nullable final String channelId
	) {
		return new PlayerChannelModel(uuid, channelId);
	}

	@Override
	public @NotNull String getId() {
		return uuid.toString();
	}

	public @NotNull UUID getUuid() {
		return uuid;
	}

	public @Nullable String getChannelId() {
		return channelId;
	}

	public void setChannelId(@Nullable final String channelId) {
		this.channelId = channelId;
	}
}
