package ml.stargirls.nova.paper.player.ignore;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentReader;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentWriter;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonWriter;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import ml.stargirls.storage.mongo.MongoModelService;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PlayerIgnoreModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerIgnoreChecker.class)
			.to(PlayerIgnoreCheckerImpl.class)
			.in(Scopes.SINGLETON);
		expose(PlayerIgnoreChecker.class);

		bind(PlayerIgnoreHandler.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public CachedRemoteModelService<PlayerIgnoreModel> provideModelService(
		@NotNull final Executor executor,
		@NotNull final MongoDatabase mongoDatabase,
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Gson gson,
		@NotNull final Configuration configuration
	) {
		String clusterId = configuration.getClusterId();

		return
			(CachedRemoteModelService<PlayerIgnoreModel>)
				MongoModelService
					.builder(PlayerIgnoreModel.class, MinecraftDocumentReader.class)
					.database(mongoDatabase)
					.collection(clusterId + "PlayerIgnoreModel")
					.executor(executor)
					.readerFactory(MinecraftDocumentReader::create)
					.modelWriter(
						object ->
							MinecraftDocumentWriter.create(object)
								.writeDetailedUuids("ignored", object.getIgnoredPlayers())
								.end())
					.modelReader(reader -> {
						UUID uuid = Validate.notNull(reader.readUuid(MongoModelService.ID_FIELD), "uuid");
						Set<UUID> ignored = Validate.notNull(
							reader.readDetailedUuids("ignored", HashSet::new),
							"ignored");
						return new PlayerIgnoreModel(uuid, ignored);
					})
					.cachedService(
						RedisModelService
							.builder(PlayerIgnoreModel.class, MinecraftJsonReader.class)
							.tableName("nova:" + clusterId + ":player-ignore")
							.gson(gson)
							.jedisPool(jedisInstance.jedisPool())
							.readerFactory(MinecraftJsonReader::create)
							.expireAfterSave(60 * 5)
							.expireAfterAccess(60 * 5)
							.modelWriter(
								object ->
									MinecraftJsonWriter
										.create()
										.writeDetailedUuid("uuid", object.getUuid())
										.writeDetailedUuids("ignored", object.getIgnoredPlayers())
										.end())
							.modelReader(reader -> {
								UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"), "uuid");
								Set<UUID> ignored = Validate.notNull(
									reader.readDetailedUuids("ignored", HashSet::new),
									"ignored");
								return new PlayerIgnoreModel(uuid, ignored);
							})
							.build())
					.build();
	}
}
