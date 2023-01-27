package ml.stargirls.nova.paper.player.property;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.mongodb.client.MongoDatabase;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentReader;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentWriter;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonWriter;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.nova.paper.player.model.PlayerModelServiceImpl;
import ml.stargirls.nova.paper.player.player.PlayerModelService;
import ml.stargirls.nova.paper.player.resolve.PlayerTargetResolver;
import ml.stargirls.storage.mongo.MongoModelService;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PlayerPropertyModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerPropertiesHandler.class).to(PlayerPropertiesHandlerImpl.class)
			.in(Scopes.SINGLETON);

		expose(PlayerPropertiesHandler.class);

		bind(PlayerPropertiesService.class).to(PlayerPropertiesServiceImpl.class)
			.in(Scopes.SINGLETON);

		expose(PlayerPropertiesService.class);
	}

	@Provides
	@Singleton
	public PlayerModelService<PlayerPropertiesModel> provideService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final MongoDatabase database,
		@NotNull final Configuration configuration,
		@NotNull final MessageHandler messageHandler,
		@NotNull final PlayerTargetResolver playerTargetResolver
	) {
		String clusterId = configuration.getClusterId();
		return new PlayerModelServiceImpl<>(
			executor,
			RedisModelService
				.builder(PlayerPropertiesModel.class, MinecraftJsonReader.class)
				.tableName("nova:" + clusterId + ":player-properties")
				.gson(gson)
				.executor(executor)
				.jedisPool(jedisInstance.jedisPool())
				.readerFactory(MinecraftJsonReader::create)
				.expireAfterSave(60 * 5)
				.expireAfterAccess(60 * 5)
				.modelWriter(
					object ->
						MinecraftJsonWriter.create()
							.writeDetailedUuid("uuid", object.uuid())
							.writeRawCollection("properties", object.properties())
							.end())
				.modelReader(reader -> {
					UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"), "uuid");
					Set<String> properties =
						reader.readRawCollection("properties", String.class, HashSet::new);

					return new PlayerPropertiesModel(
						uuid,
						properties == null ?
						new HashSet<>() :
						properties);
				})
				.build(),
			MongoModelService
				.builder(PlayerPropertiesModel.class, MinecraftDocumentReader.class)
				.collection(clusterId + "PlayerPropertyModel")
				.executor(executor)
				.database(database)
				.readerFactory(MinecraftDocumentReader::create)
				.modelWriter(
					object ->
						MinecraftDocumentWriter.create(object)
							.writeRawCollection("properties", object.properties())
							.end())
				.modelReader(reader -> {
					UUID uuid = Validate.notNull(
						reader.readUuid(MongoModelService.ID_FIELD),
						"uuid");
					Set<String> properties =
						reader.readRawCollection("properties", String.class, HashSet::new);

					return new PlayerPropertiesModel(
						uuid,
						properties == null ?
						new HashSet<>() :
						properties);
				})
				.build(),
			messageHandler,
			playerTargetResolver);
	}
}
