package ml.stargirls.nova.bungee.server;

import com.google.gson.Gson;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.storage.dist.RemoteModelService;
import ml.stargirls.storage.gson.codec.JsonReader;
import ml.stargirls.storage.gson.codec.JsonWriter;
import ml.stargirls.storage.redis.RedisModelService;
import ml.stargirls.storage.redis.connection.JedisInstance;
import ml.stargirls.storage.util.Validate;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

public class ServerModule
	extends AbstractModule {

	@Provides
	@Singleton
	public RemoteModelService<ServerInfo> provideServerInfoModelService(
		JedisInstance jedisInstance,
		Executor executor,
		Gson gson
	) {
		return (RemoteModelService<ServerInfo>) RedisModelService
			                                        .builder(
				                                        ServerInfo.class,
				                                        JsonReader.class
			                                        )
			                                        .tableName("nova:" + ServerInfo.TABLE_NAME)
			                                        .gson(gson)
			                                        .executor(executor)
			                                        .jedisPool(jedisInstance.jedisPool())
			                                        .readerFactory(JsonReader::create)
			                                        .modelWriter(object -> JsonWriter
				                                                               .create()
				                                                               .writeString(
					                                                               "id",
					                                                               object.serverIdentifier()
				                                                               )
				                                                               .end())
			                                        .modelReader(reader -> new ServerInfo(Validate.notNull(
				                                        reader.readString("id"),
				                                        "id"
			                                        )))
			                                        .build();
	}
}
