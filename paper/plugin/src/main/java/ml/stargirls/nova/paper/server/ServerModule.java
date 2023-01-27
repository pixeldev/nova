package ml.stargirls.nova.paper.server;

import com.google.gson.Gson;
import com.google.inject.Provides;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonWriter;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.storage.dist.RemoteModelService;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class ServerModule
	extends ProtectedModule {

	@Provides
	@Singleton
	public RemoteModelService<ServerInfo> provideServerInfoModelService(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Executor executor,
		@NotNull final Gson gson
	) {
		return
			(RemoteModelService<ServerInfo>)
				RedisModelService
					.builder(ServerInfo.class, MinecraftJsonReader.class)
					.tableName("nova:" + ServerInfo.TABLE_NAME)
					.gson(gson)
					.executor(executor)
					.jedisPool(jedisInstance.jedisPool())
					.readerFactory(MinecraftJsonReader::create)
					.modelWriter(
						object ->
							MinecraftJsonWriter.create()
								.writeString("id", object.serverIdentifier())
								.end())
					.modelReader(
						reader ->
							new ServerInfo(Validate.notNull(reader.readString("id"), "id")))
					.build();
	}
}
