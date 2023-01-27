package ml.stargirls.nova.paper.player.teleport;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.location.ServerLocationModel;
import ml.stargirls.nova.paper.player.permission.PermissionHelper;
import ml.stargirls.nova.paper.task.PlayerTaskRegistry;
import ml.stargirls.nova.paper.task.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

public class DelayedPlayerTeleportHandler
	implements PlayerTeleportHandler {

	@Inject
	@Named("direct")
	private PlayerTeleportHandler delegate;

	@Inject private MessageHandler messageHandler;
	@Inject private Configuration configuration;
	@Inject private PlayerTaskRegistry taskRegistry;

	@Override
	public void teleport(@NotNull final Player player, @NotNull final Player target) {
		if (canByPass(player)) {
			delegate.teleport(player, target);
			return;
		}

		UUID targetId = target.getUniqueId(); // we need it to avoid resource leaks

		taskRegistry.register(
			player,
			createBuilder(player)
				.success(taskPlayer -> {
					Player targetPlayer = Bukkit.getServer()
						                      .getPlayer(targetId);

					if (targetPlayer == null) {
						messageHandler.sendIn(taskPlayer, SendingModes.ERROR, "teleport.player-not-found");
						return;
					}

					delegate.teleport(taskPlayer, targetPlayer);
				})
				.build());
	}

	@Override
	public void teleport(@NotNull final Player player, @NotNull final Location location) {
		if (canByPass(player)) {
			delegate.teleport(player, location);
			return;
		}

		taskRegistry.register(
			player,
			createBuilder(player)
				.success(taskPlayer -> delegate.teleport(taskPlayer, location))
				.build());
	}

	@Override
	public void teleport(@NotNull final Player player, @NotNull final LocationModel locationModel) {
		if (canByPass(player)) {
			delegate.teleport(player, locationModel);
			return;
		}

		taskRegistry.register(
			player,
			createBuilder(player)
				.success(taskPlayer -> delegate.teleport(taskPlayer, locationModel))
				.build());
	}

	@Override
	public void teleport(
		@NotNull final Player player,
		@NotNull final ServerLocationModel locationModel
	) {
		if (canByPass(player)) {
			delegate.teleport(player, locationModel);
			return;
		}

		taskRegistry.register(
			player,
			createBuilder(player)
				.success(taskPlayer -> delegate.teleport(taskPlayer, locationModel))
				.build());
	}

	@Override
	public void teleport(
		@NotNull final Player player,
		@NotNull final UUID targetId
	) {
		if (canByPass(player)) {
			delegate.teleport(player, targetId);
			return;
		}

		taskRegistry.register(
			player,
			createBuilder(player)
				.success(taskPlayer -> delegate.teleport(taskPlayer, targetId))
				.build());
	}

	@Contract(pure = true, value = "_ -> new")
	private Task.Builder createBuilder(@NotNull final Player player) {
		int delay = configuration.getTeleport()
			            .getTeleportDelay();
		messageHandler.sendReplacingIn(
			player,
			SendingModes.PING,
			"teleport.delay",
			Placeholder.component("delay", Component.text(delay)));

		return Task.builder(delay)
			       .failure(taskPlayer -> messageHandler.sendIn(
				       taskPlayer,
				       SendingModes.ERROR,
				       "teleport.move-cancelled"));
	}

	private boolean canByPass(@NotNull final Player player) {
		return PermissionHelper.hasPermission(player, "teleport.cooldown.bypass");
	}
}
