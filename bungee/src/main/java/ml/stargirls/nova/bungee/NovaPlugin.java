package ml.stargirls.nova.bungee;

import ml.stargirls.nova.bungee.listener.PlayerPreConnectListener;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.redis.messenger.RedisMessenger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import team.unnamed.inject.Injector;

public class NovaPlugin
	extends Plugin {

	private Injector injector;

	@Override
	public void onLoad() {
		injector = Injector.create(new MainModule(this));
	}

	@Override
	public void onEnable() {
		ProxyServer
			.getInstance()
			.getPluginManager()
			.registerListener(
				this,
				injector.getInstance(PlayerPreConnectListener.class)
			);
	}

	@Override
	public void onDisable() {
		injector
			.getInstance(RedisMessenger.class)
			.close();

		JedisInstance jedisInstance = injector.getInstance(JedisInstance.class);
		jedisInstance
			.jedisPool()
			.close();
		jedisInstance
			.listenerConnection()
			.close();
	}
}
