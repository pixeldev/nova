package ml.stargirls.nova.paper.warp;

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

public class WarpModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(WarpManager.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public CachedRemoteModelService<Warp> createPlayerInventoryDataService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson,
		@NotNull final MongoDatabase database,
		@NotNull final Configuration configuration
	) {
		String clusterId = configuration.getClusterId();

		return
			(CachedRemoteModelService<Warp>)
				MongoModelService
					.builder(Warp.class, MinecraftDocumentReader.class)
					.collection(clusterId + "Warp")
					.executor(executor)
					.database(database)
					.readerFactory(MinecraftDocumentReader::create)
					.modelWriter(
						object ->
							MinecraftDocumentWriter.create(object)
								.writeComponent("displayName", object.getDisplayName())
								.writeBoolean("restricted", object.isRestricted())
								.writeBoolean("listed", object.isListed())
								.writeObject(
									"location",
									object.getLocation(),
									ServerLocationModelCodec.DOCUMENT_WRITER)
								.end())
					.modelReader(reader -> new Warp(
						Validate.notNull(reader.readString(MongoModelService.ID_FIELD), "id"),
						Validate.notNull(reader.readBoolean("restricted"), "restricted"),
						Validate.notNull(reader.readBoolean("listed"), "listed"),
						Validate.notNull(reader.readComponent("displayName"), "displayName"),
						Validate.notNull(
							reader.readObject(
								"location",
								ServerLocationModelCodec.DOCUMENT_READER),
							"location")))
					.cachedService(
						RedisModelService
							.builder(Warp.class, MinecraftJsonReader.class)
							.tableName("nova:" + clusterId + ":warp")
							.gson(gson)
							.jedisPool(jedisInstance.jedisPool())
							.readerFactory(MinecraftJsonReader::create)
							.modelWriter(
								object ->
									MinecraftJsonWriter
										.create()
										.writeString("id", object.getId())
										.writeComponent("displayName", object.getDisplayName())
										.writeBoolean("restricted", object.isRestricted())
										.writeBoolean("listed", object.isListed())
										.writeObject(
											"location",
											object.getLocation(),
											ServerLocationModelCodec.JSON_WRITER)
										.end())
							.modelReader(reader -> new Warp(
								Validate.notNull(reader.readString("id"), "id"),
								Validate.notNull(reader.readBoolean("restricted"), "restricted"),
								Validate.notNull(reader.readBoolean("listed"), "listed"),
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
