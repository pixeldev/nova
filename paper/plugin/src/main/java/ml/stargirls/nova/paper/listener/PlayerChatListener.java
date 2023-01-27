package ml.stargirls.nova.paper.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import ml.stargirls.nova.paper.chat.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerChatListener
	implements Listener {

	@Inject private ChatManager chatManager;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(@NotNull final AsyncChatEvent event) {
		event.setCancelled(true);
		chatManager.sendMessage(event.getPlayer(), event.message());
	}
}
