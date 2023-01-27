package ml.stargirls.nova.paper.player.home;

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
import java.util.*;
import java.util.concurrent.Executor;

public class PlayerHomeModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerHomeConfigurationHandler.class).in(Scopes.SINGLETON);
		bind(PlayerHomeHandler.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public PlayerModelService<PlayerHomeModel> provideService(
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
				.builder(PlayerHomeModel.class, MinecraftJsonReader.class)
				.tableName("nova:" + clusterId + ":player-home")
				.gson(gson)
				.executor(executor)
				.jedisPool(jedisInstance.jedisPool())
				.readerFactory(MinecraftJsonReader::create)
				.expireAfterSave(60 * 15)
				.expireAfterAccess(60 * 15)
				.modelWriter(
					object ->
						MinecraftJsonWriter.create()
							.writeDetailedUuid("uuid", object.getUuid())
							.writeRawCollection("homes", object.getHomeNames())
							.writeMap(
								"sharedHomes",
								object.getSharedHomes(),
								sharedHome -> MinecraftJsonWriter.create()
									              .writeString("alias", sharedHome.alias())
									              .writeString("realId", sharedHome.realId())
									              .end())
							.writeNumber("maxHomes", object.getMaxHomes())
							.writeNumber("maxSharedHomes", object.getMaxSharedHomes())
							.end())
				.modelReader(reader -> {
					UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"), "uuid");
					Set<String> homes =
						reader.readRawCollection("homes", String.class, HashSet::new);
					Map<String, PlayerHomeModel.Shared> sharedHomes =
						reader.readMap(
							"sharedHomes",
							PlayerHomeModel.Shared::alias,
							sharedHomeReader -> {
								String alias = Validate.notNull(
									sharedHomeReader.readString("alias"),
									"alias");
								String realId = Validate.notNull(
									sharedHomeReader.readString("realId"),
									"realId");
								return new PlayerHomeModel.Shared(alias, realId);
							});
					int maxHomes = Validate.notNull(reader.readNumber("maxHomes"), "maxHomes")
						               .intValue();
					int maxSharedHomes =
						Validate.notNull(reader.readNumber("maxSharedHomes"), "maxSharedHomes")
							.intValue();

					return new PlayerHomeModel(
						uuid,
						homes == null ?
						new HashSet<>() :
						homes,
						sharedHomes == null ?
						new HashMap<>() :
						sharedHomes,
						maxHomes,
						maxSharedHomes);
				})
				.build(),
			MongoModelService
				.builder(PlayerHomeModel.class, MinecraftDocumentReader.class)
				.collection(clusterId + "PlayerHomeModel")
				.executor(executor)
				.database(database)
				.readerFactory(MinecraftDocumentReader::create)
				.modelWriter(
					object ->
						MinecraftDocumentWriter.create(object)
							.writeRawCollection("homes", object.getHomeNames())
							.writeMap(
								"sharedHomes",
								object.getSharedHomes(),
								sharedHome -> MinecraftDocumentWriter.create()
									              .writeString("alias", sharedHome.alias())
									              .writeString("realId", sharedHome.realId())
									              .end())
							.writeNumber("maxHomes", object.getMaxHomes())
							.writeNumber("maxSharedHomes", object.getMaxSharedHomes())
							.end())
				.modelReader(reader -> {
					UUID uuid = Validate.notNull(
						reader.readUuid(MongoModelService.ID_FIELD),
						"uuid");
					Set<String> homes =
						reader.readRawCollection("homes", String.class, HashSet::new);
					Map<String, PlayerHomeModel.Shared> sharedHomes =
						reader.readMap(
							"sharedHomes",
							PlayerHomeModel.Shared::alias,
							sharedHomeReader -> {
								String alias = Validate.notNull(
									sharedHomeReader.readString("alias"),
									"alias");
								String realId = Validate.notNull(
									sharedHomeReader.readString("realId"),
									"realId");
								return new PlayerHomeModel.Shared(alias, realId);
							});
					int maxHomes = Validate.notNull(reader.readNumber("maxHomes"), "maxHomes")
						               .intValue();
					int maxSharedHomes =
						Validate.notNull(reader.readNumber("maxSharedHomes"), "maxSharedHomes")
							.intValue();

					return new PlayerHomeModel(
						uuid,
						homes == null ?
						new HashSet<>() :
						homes,
						sharedHomes == null ?
						new HashMap<>() :
						sharedHomes,
						maxHomes,
						maxSharedHomes);
				})
				.build(),
			messageHandler,
			playerTargetResolver);
	}
}
