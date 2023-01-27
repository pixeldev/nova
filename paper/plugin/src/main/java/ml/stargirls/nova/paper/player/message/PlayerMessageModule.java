package ml.stargirls.nova.paper.player.message;

import com.google.gson.Gson;
import com.google.inject.Provides;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonWriter;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.storage.dist.RemoteModelService;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PlayerMessageModule
	extends ProtectedModule {

	@Provides
	@Singleton
	public RemoteModelService<PlayerRecentMessageModel> recentMessageModelService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final Configuration configuration
	) {
		return
			(RemoteModelService<PlayerRecentMessageModel>)
				RedisModelService
					.builder(PlayerRecentMessageModel.class, MinecraftJsonReader.class)
					.tableName("nova:" + configuration.getClusterId() + ":player-message")
					.gson(gson)
					.jedisPool(jedisInstance.jedisPool())
					.executor(executor)
					.expireAfterSave(60 * 5)
					.readerFactory(MinecraftJsonReader::create)
					.modelWriter(
						object ->
							MinecraftJsonWriter
								.create()
								.writeDetailedUuid("originId", object.playerId())
								.writeDetailedUuid("lastSentPlayerId", object.lastSentPlayerId())
								.end())
					.modelReader(reader -> {
						UUID playerId = Validate.notNull(reader.readDetailedUuid("originId"));
						UUID lastSentPlayerId = Validate.notNull(reader.readDetailedUuid(
							"lastSentPlayerId"));
						return new PlayerRecentMessageModel(playerId, lastSentPlayerId);
					})
					.build();
	}
}
