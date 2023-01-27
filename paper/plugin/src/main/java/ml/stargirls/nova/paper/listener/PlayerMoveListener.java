package ml.stargirls.nova.paper.listener;

import ml.stargirls.nova.paper.task.PlayerTaskRegistry;
import ml.stargirls.nova.paper.task.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.function.Consumer;

public class PlayerMoveListener
	implements Listener {

	@Inject private PlayerTaskRegistry taskRegistry;

	@EventHandler
	public void onMove(@NotNull final PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) {
			return;
		}

		Player player = event.getPlayer();
		Task task = taskRegistry.unregister(player);

		if (task == null) {
			return;
		}

		Consumer<Player> failureAction = task.getFailureAction();

		if (failureAction == null) {
			return;
		}

		failureAction.accept(player);
	}
}
