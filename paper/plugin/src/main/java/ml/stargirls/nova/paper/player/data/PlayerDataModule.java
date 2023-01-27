package ml.stargirls.nova.paper.player.data;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.mongodb.client.MongoDatabase;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentReader;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentWriter;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonWriter;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.nova.paper.player.effect.PlayerEffect;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import ml.stargirls.storage.mongo.MongoModelService;
import ml.stargirls.storage.mongo.codec.DocumentWriter;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PlayerDataModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerDataManager.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public CachedRemoteModelService<PlayerInventoryModel> createPlayerInventoryDataService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final MongoDatabase database,
		@NotNull final Configuration configuration
	) {
		String clusterId = configuration.getClusterId();

		return
			(CachedRemoteModelService<PlayerInventoryModel>)
				MongoModelService
					.builder(PlayerInventoryModel.class, MinecraftDocumentReader.class)
					.collection(clusterId + "PlayerInventoryModel")
					.executor(executor)
					.database(database)
					.readerFactory(MinecraftDocumentReader::create)
					.modelWriter(object -> PlayerInventoryModel.write(
						MinecraftDocumentWriter.create(object),
						object))
					.modelReader(reader -> {
						UUID uuid = Validate.notNull(reader.readUuid(MongoModelService.ID_FIELD), "id");
						return PlayerInventoryModel.read(reader, uuid);
					})
					.cachedService(
						RedisModelService
							.builder(PlayerInventoryModel.class, MinecraftJsonReader.class)
							.tableName("nova:" + clusterId + ":player-inventory-data")
							.gson(gson)
							.jedisPool(jedisInstance.jedisPool())
							.readerFactory(MinecraftJsonReader::create)
							.modelWriter(object -> {
								MinecraftJsonWriter writer =
									MinecraftJsonWriter.create()
										.writeDetailedUuid("uuid", object.playerId());
								return PlayerInventoryModel.write(writer, object);
							})
							.modelReader(reader -> {
								UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"), "uuid");
								return PlayerInventoryModel.read(
									reader,
									uuid
								);
							})
							.expireAfterSave(60 * 5)
							.build())
					.build();
	}

	@Provides
	@Singleton
	public CachedRemoteModelService<PlayerDataModel> createPlayerDataService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final MongoDatabase database,
		@NotNull final Configuration configuration
	) {
		String clusterId = configuration.getClusterId();

		return
			(CachedRemoteModelService<PlayerDataModel>)
				MongoModelService
					.builder(PlayerDataModel.class, MinecraftDocumentReader.class)
					.collection(clusterId + "PlayerDataModel")
					.executor(executor)
					.database(database)
					.readerFactory(MinecraftDocumentReader::create)
					.modelWriter(object -> PlayerDataModel.write(
						MinecraftDocumentWriter.create(object),
						effect -> DocumentWriter.create()
							          .writeNumber("effectId", effect.effectId())
							          .writeNumber("amplifier", effect.amplifier())
							          .writeNumber("duration", effect.duration())
							          .end(),
						object))
					.modelReader(reader -> {
						UUID uuid = Validate.notNull(reader.readUuid(MongoModelService.ID_FIELD), "id");
						return PlayerDataModel.read(
							reader,
							effectReader ->
								new PlayerEffect(
									Validate.notNull(effectReader.readNumber("effectId"))
										.intValue(),
									Validate.notNull(effectReader.readNumber("amplifier"))
										.intValue(),
									Validate.notNull(effectReader.readNumber("duration"))
										.intValue()),
							uuid);
					})
					.cachedService(
						RedisModelService
							.builder(PlayerDataModel.class, MinecraftJsonReader.class)
							.tableName("nova:" + clusterId + ":player-data")
							.gson(gson)
							.jedisPool(jedisInstance.jedisPool())
							.readerFactory(MinecraftJsonReader::create)
							.modelWriter(object -> {
								MinecraftJsonWriter writer =
									MinecraftJsonWriter.create()
										.writeDetailedUuid("uuid", object.playerId());
								return PlayerDataModel.write(
									writer,
									model ->
										MinecraftJsonWriter.create()
											.writeNumber("effectId", model.effectId())
											.writeNumber(
												"amplifier",
												model.amplifier())
											.writeNumber("duration", model.duration())
											.end(),
									object);
							})
							.modelReader(reader -> {
								UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"), "uuid");
								return PlayerDataModel.read(
									reader,
									effectReader ->
										new PlayerEffect(
											Validate.notNull(
													effectReader.readNumber("effectId"))
												.intValue(),
											Validate.notNull(
													effectReader.readNumber(
														"amplifier"))
												.intValue(),
											Validate.notNull(
													effectReader.readNumber(
														"duration"))
												.intValue()),
									uuid);
							})
							.expireAfterSave(60 * 5)
							.build())
					.build();
	}
}
