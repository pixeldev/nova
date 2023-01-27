package ml.stargirls.nova.paper.chat.channel;

import ml.stargirls.nova.paper.chat.channel.player.PlayerChannelModel;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatChannelServiceImpl
	implements ChatChannelService {

	@Inject private CachedRemoteModelService<PlayerChannelModel> playerChannelModelService;

	private final Map<String, ChatChannel> channels = new ConcurrentHashMap<>();
	private final Map<Character, String> channelsByPrefix = new ConcurrentHashMap<>();

	@Override
	public @NotNull Collection<String> getChannelIds() {
		return channels.keySet();
	}

	@Override
	public @Nullable ChatChannel getCurrentChannel(@NotNull final UUID playerId) {
		PlayerChannelModel model = playerChannelModelService.getSync(playerId.toString());

		if (model == null) {
			return null;
		}

		return getById(model.getChannelId());
	}

	@Override
	public @Nullable PlayerChannelModel getPlayerChannelModel(@NotNull final UUID playerId) {
		return playerChannelModelService.getSync(playerId.toString());
	}

	@Override
	public @Nullable ChatChannel getByChar(final char prefix) {
		String id = channelsByPrefix.get(prefix);

		if (id == null) {
			return null;
		}

		return channels.get(id);
	}

	@Override
	public @Nullable ChatChannel getById(@Nullable final String id) {
		if (id == null) {
			return null;
		}

		return channels.get(id);
	}

	@Override
	public void registerChannel(@NotNull final ChatChannel channel) {
		String id = channel.getId();
		channels.put(id, channel);

		char prefix = channel.getPrefix();
		if (prefix != ChatChannel.NO_PREFIX) {
			channelsByPrefix.put(prefix, id);
		}
	}
}
