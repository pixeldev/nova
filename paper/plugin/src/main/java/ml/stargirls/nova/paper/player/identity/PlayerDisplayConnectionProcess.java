package ml.stargirls.nova.paper.player.identity;

import ml.stargirls.maia.paper.notifier.MessageNotifier;
import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.nova.paper.player.connection.ConnectionContext;
import ml.stargirls.nova.paper.player.connection.ConnectionProcess;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

public class PlayerDisplayConnectionProcess
	implements ConnectionProcess {

	@Inject private PlayerModelService<PlayerDisplayIdentityModel> modelService;
	@Inject private MessageNotifier messageNotifier;

	@Override
	public void processConnect(@NotNull final ConnectionContext context) throws Exception {
		Player player = context.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerDisplayIdentityModel displayIdentityModel =
			modelService.getOrFindSync(uuid.toString());

		User user = LuckPermsProvider.get()
			            .getUserManager()
			            .getUser(uuid);

		if (user == null) {
			throw new Exception("LuckPerms user not found");
		}

		String prefix = user.getCachedData()
			                .getMetaData()
			                .getPrefix();
		Component prefixComponent;

		if (prefix == null) {
			Group group = LuckPermsProvider.get()
				              .getGroupManager()
				              .getGroup(user.getPrimaryGroup());

			if (group != null) {
				prefix = group.getCachedData()
					         .getMetaData()
					         .getPrefix();
			}
		}

		prefixComponent = prefix == null ?
		                  Component.empty() :
		                  LegacyComponentSerializer.legacyAmpersand()
			                  .deserialize(prefix);

		if (displayIdentityModel == null) {
			displayIdentityModel = PlayerDisplayIdentityModel.create(player, prefixComponent);
		} else {
			player.displayName(displayIdentityModel.getDisplayName());
			displayIdentityModel.setPrefix(prefixComponent);
		}

		String path = "join." + user.getPrimaryGroup();
		TagResolver resolver = Placeholder.component("player", displayIdentityModel.getDisplayName());

		messageNotifier.sendNotification(Notification.global(path, resolver));
		messageNotifier.sendReplacing(Bukkit.getConsoleSender(), path, resolver);
		modelService.saveSync(displayIdentityModel);
	}

	@Override
	public void processDisconnect(@NotNull final ConnectionContext context)
		throws Exception {
		User user = LuckPermsProvider.get()
			            .getUserManager()
			            .getUser(context.getPlayer()
				                     .getUniqueId());

		if (user == null) {
			throw new Exception("LuckPerms user not found");
		}

		String path = "leave." + user.getPrimaryGroup();
		TagResolver resolver = Placeholder.component(
			"player",
			context.getPlayer()
				.displayName());

		messageNotifier.sendNotification(Notification.global(path, resolver));
		messageNotifier.sendReplacing(Bukkit.getConsoleSender(), path, resolver);
		modelService.deleteInCacheSync(context.getPlayerIdAsString());
	}
}
