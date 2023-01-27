package ml.stargirls.nova.bungee.redis;

import com.google.gson.Gson;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.nova.bungee.config.Configuration;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.redis.messenger.RedisMessenger;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class RedisModule
	extends AbstractModule {

	@Provides
	@Singleton
	public JedisInstance provideJedisInstance(Configuration configuration) {
		Configuration.Redis redis = configuration.redis();

		return JedisInstance
			       .builder()
			       .setTimeout(redis.timeout())
			       .setHost(redis.host())
			       .setPort(redis.port())
			       .setPassword(redis.password())
			       .build();
	}

	@Provides
	@Singleton
	public RedisMessenger provideRedis(
		Executor executor,
		Gson gson,
		JedisInstance jedisInstance
	) {
		return new RedisMessenger(
			"nova",
			ServerInfo.PROXY_SERVER.getId(),
			executor,
			gson,
			jedisInstance
		);
	}
}
