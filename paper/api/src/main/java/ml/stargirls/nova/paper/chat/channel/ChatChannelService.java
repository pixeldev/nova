package ml.stargirls.nova.paper.chat.channel;

import ml.stargirls.nova.paper.chat.channel.player.PlayerChannelModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface ChatChannelService {

	@NotNull Collection<String> getChannelIds();

	@Nullable ChatChannel getCurrentChannel(@NotNull UUID playerId);

	@Nullable PlayerChannelModel getPlayerChannelModel(@NotNull UUID playerId);

	@Nullable ChatChannel getByChar(char prefix);

	@Nullable ChatChannel getById(@Nullable String id);

	void registerChannel(@NotNull ChatChannel channel);
}
