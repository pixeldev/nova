package ml.stargirls.nova.bungee.player;

import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.storage.redis.channel.RedisChannel;
import ml.stargirls.storage.redis.messenger.RedisMessenger;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

import javax.inject.Singleton;

public class PlayerModule
	extends AbstractModule {

	@Override
	public void configure() {
		bind(PlayerServerChangeRegistry.class).singleton();
	}

	@Provides
	@Singleton
	public RedisChannel<ServerChangeRequest> providePlayerServerChangeChannel(
		RedisMessenger messenger,
		PlayerServerChangeChannelListener listener
	) {
		return messenger
			       .getChannel(
				       ServerChangeRequest.CHANNEL_ID,
				       ServerChangeRequest.class
			       )
			       .addListener(listener);
	}
}
