package ml.stargirls.nova.paper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import ml.stargirls.maia.inject.ProtectedBinder;
import ml.stargirls.maia.paper.command.CommandRegistrationBuilder;
import ml.stargirls.maia.paper.inject.InjectedPlugin;
import ml.stargirls.maia.paper.listener.ListenerRegistrationBuilder;
import ml.stargirls.maia.paper.notifier.filter.MessageNotifierFilterRegistry;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.nova.paper.chat.channel.PlayerChannelConnectionProcess;
import ml.stargirls.nova.paper.command.*;
import ml.stargirls.nova.paper.command.factory.ChatChannelPartFactory;
import ml.stargirls.nova.paper.command.factory.PlayerServerModelPartFactory;
import ml.stargirls.nova.paper.command.factory.WarpPartFactory;
import ml.stargirls.nova.paper.listener.PlayerChatListener;
import ml.stargirls.nova.paper.listener.PlayerConnectionListener;
import ml.stargirls.nova.paper.listener.PlayerDataListener;
import ml.stargirls.nova.paper.listener.PlayerMoveListener;
import ml.stargirls.nova.paper.message.SocialSpyMessageNotifierFilter;
import ml.stargirls.nova.paper.player.connection.ConnectionProcessRegistry;
import ml.stargirls.nova.paper.player.data.PlayerDataConnectionProcess;
import ml.stargirls.nova.paper.player.data.PlayerDataManager;
import ml.stargirls.nova.paper.player.identity.PlayerDisplayConnectionProcess;
import ml.stargirls.nova.paper.player.property.NovaPlayerProperties;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesConnectionProcess;
import ml.stargirls.nova.paper.player.property.PlayerPropertiesService;
import ml.stargirls.nova.paper.player.resolve.PlayerResolverConnectionProcess;
import ml.stargirls.nova.paper.player.server.PlayerServerConnectionProcess;
import ml.stargirls.nova.paper.player.teleport.PlayerTeleportConnectionProcess;
import ml.stargirls.nova.paper.task.PlayerTaskConnectionProcess;
import ml.stargirls.storage.dist.RemoteModelService;
import ml.stargirls.storage.redis.messenger.RedisMessenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class NovaPlugin
	extends JavaPlugin
	implements InjectedPlugin {

	@Inject private Injector injector;

	@Override
	public void onLoad() {
		ServerInfo serverInfo = this.injector.getInstance(ServerInfo.class);
		injector.getInstance(Key.get(new TypeLiteral<RemoteModelService<ServerInfo>>() { }))
			.saveSync(serverInfo);

		ConnectionProcessRegistry processRegistry =
			injector.getInstance(ConnectionProcessRegistry.class);

		processRegistry.register(injector.getInstance(PlayerResolverConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerDataConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerServerConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerChannelConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerDisplayConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerPropertiesConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerTaskConnectionProcess.class));
		processRegistry.register(injector.getInstance(PlayerTeleportConnectionProcess.class));

		MessageNotifierFilterRegistry filterRegistry =
			injector.getInstance(MessageNotifierFilterRegistry.class);

		filterRegistry.registerFilter(injector.getInstance(SocialSpyMessageNotifierFilter.class));

		PlayerPropertiesService playerPropertiesService =
			injector.getInstance(PlayerPropertiesService.class);

		playerPropertiesService.registerDefaultProperty(NovaPlayerProperties.MENTIONS);
		playerPropertiesService.registerDefaultProperty(NovaPlayerProperties.MESSAGES);
	}

	@Override
	public void onEnable() {
		try {
			ListenerRegistrationBuilder.create(4)
				.add(PlayerChatListener.class)
				.add(PlayerConnectionListener.class)
				.add(PlayerDataListener.class)
				.add(PlayerMoveListener.class)
				.build()
				.execute(this, injector);

			CommandRegistrationBuilder
				.create(9, 2, 1)
				.addCommand(ChatCommand.class)
				.addCommand(DisplayCommand.class)
				.addCommand(ExternalMessageCommand.class)
				.addCommand(MessageCommand.class)
				.addCommand(SpawnCommand.class)
				.addCommand(WarpCommand.class)
				.addCommand(IgnoreCommand.class)
				.addCommand(HomeCommand.class)
				.addCommand(MentionCommand.class)
				.addInjectablePartFactory(ChatChannelPartFactory.class)
				.addAsyncCompletableFactory(PlayerServerModelPartFactory.class)
				.addAsyncCompletableFactory(WarpPartFactory.class)
				.build()
				.execute(this, injector);
		} catch (Exception exception) {
			getSLF4JLogger().error("Failed to enable nova", exception);
			Bukkit.shutdown();
		}
	}

	@Override
	public void onDisable() {
		try {
			ServerInfo serverInfo = injector.getInstance(ServerInfo.class);
			injector.getInstance(Key.get(new TypeLiteral<RemoteModelService<ServerInfo>>() { }))
				.deleteSync(serverInfo.getId());

			PlayerDataManager dataManager = injector.getInstance(PlayerDataManager.class);

			for (Player player : Bukkit.getOnlinePlayers()) {
				dataManager.saveAllSync(player);
			}

			injector.getInstance(RedisMessenger.class)
				.close();
		} catch (Exception e) {
			getSLF4JLogger().warn("Failed to disable nova", e);
		}
	}

	@Override
	public void configure(@NotNull final ProtectedBinder protectedBinder) {
		protectedBinder.install(new MainModule());
	}
}
