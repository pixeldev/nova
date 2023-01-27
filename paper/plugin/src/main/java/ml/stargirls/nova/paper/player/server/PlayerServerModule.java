package ml.stargirls.nova.paper.player.server;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.nova.paper.player.server.change.PlayerServerChangeChannelListener;
import ml.stargirls.storage.dist.RemoteModelService;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.channel.RedisChannel;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.redis.messenger.RedisMessenger;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class PlayerServerModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerServerService.class)
			.to(PlayerServerServiceImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerServerService.class);

		bind(PlayerServerSender.class)
			.to(PlayerServerSenderImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerServerService.class);
	}

	@Provides
	@Singleton
	public RemoteModelService<PlayerServerModel> createPlayerServerDataService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final Configuration configuration
	) {
		return
			(RemoteModelService<PlayerServerModel>)
				RedisModelService
					.builder(PlayerServerModel.class, MinecraftJsonReader.class)
					.tableName("nova:" + configuration.getClusterId() + ":player-server")
					.gson(gson)
					.executor(executor)
					.jedisPool(jedisInstance.jedisPool())
					.readerFactory(MinecraftJsonReader::create)
					.modelWriter(PlayerServerModelCodec.WRITER)
					.modelReader(PlayerServerModelCodec.READER)
					.build();
	}

	@Provides
	@Singleton
	public RedisChannel<ServerChangeRequest> provideServerChangeChannel(
		@NotNull final RedisMessenger messenger,
		@NotNull final PlayerServerChangeChannelListener listener
	) {
		return messenger.getChannel(ServerChangeRequest.CHANNEL_ID, ServerChangeRequest.class)
			       .addListener(listener);
	}
}
