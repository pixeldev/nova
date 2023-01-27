package ml.stargirls.nova.bungee;

import ml.stargirls.nova.bungee.codec.GsonModule;
import ml.stargirls.nova.bungee.config.Configuration;
import ml.stargirls.nova.bungee.config.ConfigurationModule;
import ml.stargirls.nova.bungee.player.PlayerModule;
import ml.stargirls.nova.bungee.redis.RedisModule;
import ml.stargirls.nova.bungee.server.ServerModule;
import net.md_5.bungee.api.plugin.Plugin;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainModule
	extends AbstractModule {

	private final Plugin plugin;

	public MainModule(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void configure() {
		install(
			new ConfigurationModule(),
			new RedisModule(),
			new GsonModule(),
			new PlayerModule(),
			new ServerModule()
		);

		bind(Plugin.class).toInstance(plugin);
	}

	@Provides
	@Singleton
	public Executor provideExecutor(Configuration configuration) {
		return Executors.newFixedThreadPool(configuration.threads());
	}
}
