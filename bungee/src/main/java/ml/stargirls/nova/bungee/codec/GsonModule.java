package ml.stargirls.nova.bungee.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.maia.server.ServerInfoCodec;
import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.maia.server.request.ServerChangeRequestCodec;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

import javax.inject.Singleton;

public class GsonModule
	extends AbstractModule {

	@Provides
	@Singleton
	public Gson provideGson() {
		return new GsonBuilder()
			       .registerTypeAdapter(
				       ServerInfo.class,
				       new ServerInfoCodec()
			       )
			       .registerTypeAdapter(
				       ServerChangeRequest.class,
				       new ServerChangeRequestCodec()
			       )
			       .create();
	}
}
