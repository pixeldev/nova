package ml.stargirls.nova.paper.player.property;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerPropertiesServiceImpl
	implements PlayerPropertiesService {

	private final Set<String> defaultProperties = new HashSet<>();

	@Override
	public void registerDefaultProperty(@NotNull final String key) {
		defaultProperties.add(key);
	}

	@Override
	public boolean isDefaultProperty(@NotNull final String key) {
		return defaultProperties.contains(key);
	}

	@Override
	public @NotNull PlayerPropertiesModel createModelWithDefaultProperties(
		@NotNull final UUID playerId
	) {
		return new PlayerPropertiesModel(playerId, cloneDefaultProperties());
	}

	@Override
	public @NotNull Set<String> cloneDefaultProperties() {
		return new HashSet<>(defaultProperties);
	}
}
