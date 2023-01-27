package ml.stargirls.nova.paper.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationModule
	extends AbstractModule {

	@Provides
	@Singleton
	public Configuration provideConfiguration(@NotNull final Plugin plugin) throws IOException {
		Path path = plugin.getDataFolder()
			            .toPath();

		if (Files.notExists(path)) {
			Files.createDirectories(path);
		}

		path = path.resolve("config.yml");
		YamlConfigurationLoader loader =
			YamlConfigurationLoader
				.builder()
				.path(path)
				.indent(4)
				.nodeStyle(NodeStyle.BLOCK)
				.defaultOptions(options -> options.shouldCopyDefaults(true))
				.build();

		ConfigurationNode node = loader.load();
		Configuration configuration;

		if (Files.notExists(path)) {
			Files.createFile(path);

			configuration = new Configuration();
			node.set(Configuration.class, configuration);
			loader.save(node);
		} else {
			configuration = node.get(Configuration.class);
		}

		return configuration;
	}
}
