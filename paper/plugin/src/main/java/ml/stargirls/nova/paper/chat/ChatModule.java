package ml.stargirls.nova.paper.chat;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Exposed;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.mongodb.client.MongoDatabase;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentReader;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentWriter;
import ml.stargirls.nova.paper.chat.channel.*;
import ml.stargirls.nova.paper.chat.channel.player.PlayerChannelModel;
import ml.stargirls.nova.paper.chat.mention.MentionManager;
import ml.stargirls.nova.paper.chat.mention.SpacedMentionManager;
import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.storage.caffeine.CaffeineModelService;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import ml.stargirls.storage.mongo.MongoModelService;
import ml.stargirls.storage.util.Validate;
import org.ahocorasick.trie.Trie;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ChatModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(ChatChannelService.class)
			.to(ChatChannelServiceImpl.class)
			.in(Scopes.SINGLETON);
		expose(ChatChannelService.class);

		bind(ChatChannel.class)
			.to(GlobalChatChannel.class)
			.in(Scopes.SINGLETON);
		expose(ChatChannel.class);

		bind(ChatChannelHandler.class)
			.to(ChatChannelHandlerImpl.class)
			.in(Scopes.SINGLETON);
		expose(ChatChannelHandler.class);

		bind(BadWordChecker.class)
			.to(BadWordCheckerImpl.class)
			.in(Scopes.SINGLETON);
		expose(BadWordChecker.class);

		bind(MentionManager.class)
			.to(SpacedMentionManager.class)
			.in(Scopes.SINGLETON);
		expose(MentionManager.class);
	}

	@Provides
	@Singleton
	public CachedRemoteModelService<PlayerChannelModel> provideChannelModelService(
		@NotNull final MongoDatabase database,
		@NotNull final Executor executor,
		@NotNull final Configuration configuration
	) {
		return
			(CachedRemoteModelService<PlayerChannelModel>)
				MongoModelService
					.builder(PlayerChannelModel.class, MinecraftDocumentReader.class)
					.database(database)
					.collection(configuration.getClusterId() + "PlayerChannelModel")
					.executor(executor)
					.cachedService(CaffeineModelService.create(
						Caffeine
							.newBuilder()
							.executor(executor)
							.expireAfterWrite(10, TimeUnit.MINUTES)
							.expireAfterAccess(10, TimeUnit.MINUTES)
							.build()))
					.readerFactory(MinecraftDocumentReader::create)
					.modelWriter(object -> MinecraftDocumentWriter.create(object)
						                       .writeString("channelId", object.getChannelId())
						                       .end())
					.modelReader(reader -> {
						UUID uuid = Validate.notNull(reader.readUuid(MongoModelService.ID_FIELD), "uuid");
						String channelId = reader.readString("channelId");
						return PlayerChannelModel.create(uuid, channelId);
					})
					.build();
	}

	@Provides
	@Singleton
	@Exposed
	public Trie provideBadWordsTrie(@NotNull final Configuration configuration) {
		return Trie.builder()
			       .ignoreOverlaps()
			       .ignoreCase()
			       .stopOnHit()
			       .addKeywords(configuration.getChat()
				                    .getBadWords())
			       .build();
	}
}
