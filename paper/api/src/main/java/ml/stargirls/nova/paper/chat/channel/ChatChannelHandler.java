package ml.stargirls.nova.paper.chat.channel;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ChatChannelHandler {

	void joinToChannel(@NotNull Player player, @NotNull ChatChannel channel);

	void toggleChannel(@NotNull Player player, @NotNull ChatChannel channel);

	void exitFromChannel(@NotNull Player player);
}
