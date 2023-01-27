package ml.stargirls.nova.paper.home;

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
import ml.stargirls.nova.paper.location.ServerLocationModelCodec;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import ml.stargirls.storage.mongo.MongoModelService;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class HomeModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(HomeHandler.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public CachedRemoteModelService<HomeModel> createPlayerInventoryDataService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final MongoDatabase database,
		@NotNull final Configuration configuration
	) {
		String clusterId = configuration.getClusterId();

		return
			(CachedRemoteModelService<HomeModel>)
				MongoModelService
					.builder(HomeModel.class, MinecraftDocumentReader.class)
					.collection(clusterId + "HomeModel")
					.executor(executor)
					.database(database)
					.readerFactory(MinecraftDocumentReader::create)
					.modelWriter(
						object ->
							MinecraftDocumentWriter.create(object)
								.writeString("name", object.getName())
								.writeDetailedUuid("owner", object.getOwnerId())
								.writeDate("creation", object.getCreation())
								.writeMap(
									"sharingPlayers",
									object.getSharingPlayers(),
									sharing -> MinecraftDocumentWriter.create()
										           .writeDate("lastUse", sharing.getLastUse())
										           .writeDetailedUuid("playerId", sharing.getPlayerId())
										           .end())
								.writeBoolean("enabled", object.isEnabled())
								.writeDate("lastUse", object.getLastUse())
								.writeComponent("displayName", object.getDisplayName())
								.writeObject(
									"location",
									object.getLocation(),
									ServerLocationModelCodec.DOCUMENT_WRITER)
								.end())
					.modelReader(reader -> new HomeModel(
						Validate.notNull(reader.readString("name"), "name"),
						Validate.notNull(reader.readDetailedUuid("owner"), "owner"),
						Validate.notNull(reader.readDate("creation"), "creation"),
						Validate.notNull(reader.readMap(
							"sharingPlayers",
							HomeModel.Shared::getPlayerId,
							sharedReader -> new HomeModel.Shared(
								Validate.notNull(sharedReader.readDetailedUuid("playerId"), "playerId"),
								Validate.notNull(sharedReader.readDate("lastUse"), "lastUse")))),
						Validate.notNull(reader.readBoolean("enabled"), "enabled"),
						Validate.notNull(reader.readDate("lastUse"), "lastUse"),
						Validate.notNull(reader.readComponent("displayName"), "displayName"),
						Validate.notNull(
							reader.readObject(
								"location",
								ServerLocationModelCodec.DOCUMENT_READER),
							"location")))
					.cachedService(
						RedisModelService
							.builder(HomeModel.class, MinecraftJsonReader.class)
							.tableName("nova:" + clusterId + ":home")
							.gson(gson)
							.jedisPool(jedisInstance.jedisPool())
							.expireAfterAccess(10 * 60)
							.expireAfterSave(10 * 60)
							.readerFactory(MinecraftJsonReader::create)
							.modelWriter(
								object ->
									MinecraftJsonWriter.create()
										.writeString("name", object.getName())
										.writeDetailedUuid("owner", object.getOwnerId())
										.writeDate("creation", object.getCreation())
										.writeMap(
											"sharingPlayers",
											object.getSharingPlayers(),
											sharing -> MinecraftJsonWriter.create()
												           .writeDate("lastUse", sharing.getLastUse())
												           .writeDetailedUuid("playerId", sharing.getPlayerId())
												           .end())
										.writeBoolean("enabled", object.isEnabled())
										.writeDate("lastUse", object.getLastUse())
										.writeComponent("displayName", object.getDisplayName())
										.writeObject(
											"location",
											object.getLocation(),
											ServerLocationModelCodec.JSON_WRITER)
										.end())
							.modelReader(reader -> new HomeModel(
								Validate.notNull(reader.readString("name"), "name"),
								Validate.notNull(reader.readDetailedUuid("owner"), "owner"),
								Validate.notNull(reader.readDate("creation"), "creation"),
								Validate.notNull(reader.readMap(
									"sharingPlayers",
									HomeModel.Shared::getPlayerId,
									sharedReader -> new HomeModel.Shared(
										Validate.notNull(sharedReader.readDetailedUuid("playerId"), "playerId"),
										Validate.notNull(sharedReader.readDate("lastUse"), "lastUse")))),
								Validate.notNull(reader.readBoolean("enabled"), "enabled"),
								Validate.notNull(reader.readDate("lastUse"), "lastUse"),
								Validate.notNull(reader.readComponent("displayName"), "displayName"),
								Validate.notNull(
									reader.readObject(
										"location",
										ServerLocationModelCodec.JSON_READER),
									"location")))
							.build())
					.build();
	}
}
