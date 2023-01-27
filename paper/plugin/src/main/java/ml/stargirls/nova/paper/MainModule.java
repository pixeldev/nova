package ml.stargirls.nova.paper;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.inject.RedisMessengerModule;
import ml.stargirls.maia.paper.notifier.MessageNotifierModule;
import ml.stargirls.nova.paper.chat.ChatModule;
import ml.stargirls.nova.paper.component.ComponentModule;
import ml.stargirls.nova.paper.concurrent.ConcurrentModule;
import ml.stargirls.nova.paper.config.ConfigurationModule;
import ml.stargirls.nova.paper.home.HomeModule;
import ml.stargirls.nova.paper.luckperms.LuckPermsModule;
import ml.stargirls.nova.paper.message.MessageModule;
import ml.stargirls.nova.paper.player.PlayerModule;
import ml.stargirls.nova.paper.server.ServerModule;
import ml.stargirls.nova.paper.storage.GsonModule;
import ml.stargirls.nova.paper.task.TaskModule;
import ml.stargirls.nova.paper.translation.TranslationModule;
import ml.stargirls.nova.paper.warp.WarpModule;
import org.jetbrains.annotations.NotNull;

public class MainModule
	extends ProtectedModule {

	@Override
	public void configure() {
		install(new PlayerModule());
		install(new ComponentModule());
		install(new ConcurrentModule());
		install(new GsonModule());
		install(new TaskModule());
		install(new TranslationModule());
		install(new ChatModule());
		install(new ServerModule());
		install(new MessageModule());
		install(new ConfigurationModule());
		install(new WarpModule());
		install(new HomeModule());
		install(new LuckPermsModule());

		install(new MessageNotifierModule());
		install(new RedisMessengerModule("nova"));
	}

	@Provides
	@Singleton
	public MongoDatabase provideDatabase(@NotNull final MongoClient client) {
		return client.getDatabase("nova");
	}
}
