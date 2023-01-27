package ml.stargirls.nova.paper.translation;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import ml.stargirls.maia.inject.ProtectedModule;
import ml.stargirls.maia.paper.notifier.filter.MessageNotifierFilterRegistry;
import ml.stargirls.maia.paper.translation.ComponentMessageSender;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.message.bukkit.BukkitMessageAdapt;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

public class TranslationModule
	extends ProtectedModule {

	@Override
	public void configure() {
		bind(MessageNotifierFilterRegistry.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public MessageHandler createMessageHandler(
		@NotNull final Plugin plugin
	) {
		return MessageHandler.of(
			BukkitMessageAdapt.newYamlSource(plugin),
			handle -> handle
				          .specify(CommandSender.class)
				          .setLinguist(sender -> "es")
				          .setMessageSender(new ComponentMessageSender())
		);
	}
}
