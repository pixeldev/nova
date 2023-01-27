package ml.stargirls.nova.paper.player.server;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerServerSender {

	boolean sendToServerSync(@NotNull Player player, @NotNull String destination) throws Exception;
}
