package ml.stargirls.nova.paper.player.home;

import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.nova.paper.luckperms.LuckPermsHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Map;

public class PlayerHomeConfigurationHandler {

	@Inject private Configuration configuration;
	@Inject private LuckPermsHandler luckPermsHandler;

	public boolean isInvalidName(@NotNull final String name) {
		return !configuration.getHome().getNamePattern().matcher(name).matches();
	}

	public int getDefaultLimit(@NotNull final Player player) {
		String group = luckPermsHandler.getGroup(player.getUniqueId());
		Map<String, Integer> maximumHomes = configuration.getHome()
			                                    .getMaximum();
		return maximumHomes.getOrDefault(group, maximumHomes.get("default"));
	}

	public int getDefaultSharedLimit(@NotNull final Player player) {
		String group = luckPermsHandler.getGroup(player.getUniqueId());
		Map<String, Integer> maximumHomes = configuration.getHome()
			                                    .getMaximumShared();
		return maximumHomes.getOrDefault(group, maximumHomes.get("default"));
	}
}
