package ml.stargirls.nova.paper.player.identity;

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
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.Executor;

public class PlayerDisplayIdentityModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(PlayerDisplayIdentityHandler.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public PlayerModelService<PlayerDisplayIdentityModel> provideService(
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
				.builder(PlayerDisplayIdentityModel.class, MinecraftJsonReader.class)
				.tableName("nova:" + clusterId + ":player-display")
				.gson(gson)
				.executor(executor)
				.jedisPool(jedisInstance.jedisPool())
				.readerFactory(MinecraftJsonReader::create)
				.expireAfterSave(60 * 5)
				.expireAfterAccess(60 * 5)
				.modelWriter(object -> {
					MinecraftJsonWriter writer =
						MinecraftJsonWriter.create()
							.writeDetailedUuid("uuid", object.getPlayerId())
							.writeComponent("displayName", object.getDisplayName())
							.writeComponent("prefix", object.getPrefix());

					TextColor chatColor = object.getChatColor();

					if (chatColor != null) {
						writer.writeNumber("chatColor", chatColor.value());
					}

					return writer.end();
				})
				.modelReader(reader -> {
					UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"), "uuid");
					Number chatColorNumber = reader.readNumber("chatColor");
					TextColor chatColor = chatColorNumber == null ?
					                      null :
					                      TextColor.color(chatColorNumber.intValue());

					return new PlayerDisplayIdentityModel(
						uuid,
						Validate.notNull(reader.readComponent("displayName"), "displayName"),
						Validate.notNull(reader.readComponent("prefix"), "prefix"),
						chatColor);
				})
				.build(),
			MongoModelService
				.builder(PlayerDisplayIdentityModel.class, MinecraftDocumentReader.class)
				.collection(clusterId + "PlayerDisplayModel")
				.executor(executor)
				.database(database)
				.readerFactory(MinecraftDocumentReader::create)
				.modelWriter(object -> {
					MinecraftDocumentWriter writer =
						MinecraftDocumentWriter.create(object)
							.writeComponent("displayName", object.getDisplayName())
							.writeComponent("prefix", object.getPrefix());

					TextColor chatColor = object.getChatColor();

					if (chatColor != null) {
						writer.writeNumber("chatColor", chatColor.value());
					}

					return writer.end();
				})
				.modelReader(reader -> {
					UUID uuid = Validate.notNull(reader.readUuid(MongoModelService.ID_FIELD), "uuid");
					Number chatColorNumber = reader.readNumber("chatColor");
					TextColor chatColor = chatColorNumber == null ?
					                      null :
					                      TextColor.color(chatColorNumber.intValue());

					return new PlayerDisplayIdentityModel(
						uuid,
						Validate.notNull(reader.readComponent("displayName"), "displayName"),
						Validate.notNull(reader.readComponent("prefix"), "prefix"),
						chatColor);
				})
				.build(),
			messageHandler,
			playerTargetResolver);
	}
}
