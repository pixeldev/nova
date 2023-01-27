package ml.stargirls.nova.paper.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provides;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.notifier.notification.Notification;
import ml.stargirls.maia.paper.notifier.notification.NotificationCodec;
import ml.stargirls.maia.server.ServerInfo;
import ml.stargirls.maia.server.ServerInfoCodec;
import ml.stargirls.maia.server.request.ServerChangeRequest;
import ml.stargirls.maia.server.request.ServerChangeRequestCodec;

import javax.inject.Singleton;

public class GsonModule
	extends ProtectedModule {

	@Provides
	@Singleton
	public Gson createGson() {
		return new GsonBuilder().registerTypeAdapter(Notification.class, new NotificationCodec())
			       .registerTypeAdapter(ServerInfo.class, new ServerInfoCodec())
			       .registerTypeAdapter(ServerChangeRequest.class, new ServerChangeRequestCodec())
			       .create();
	}
}
