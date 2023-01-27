package ml.stargirls.nova.bungee.config;

import net.md_5.bungee.api.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationModule
	extends AbstractModule {

	@Provides
	@Singleton
	public Configuration provideConfiguration(Plugin plugin) throws IOException {
		Path path = plugin
			            .getDataFolder()
			            .toPath();

		if (Files.notExists(path)) {
			Files.createDirectories(path);
		}

		path = path.resolve("config.yml");
		YamlConfigurationLoader loader = YamlConfigurationLoader
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
			node.set(
				Configuration.class,
				configuration
			);
			loader.save(node);
		} else {
			configuration = node.get(Configuration.class);
		}

		return configuration;
	}
}
