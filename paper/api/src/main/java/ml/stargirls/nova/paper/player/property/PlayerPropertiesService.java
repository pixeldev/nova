package ml.stargirls.nova.paper.player.property;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface PlayerPropertiesService {

	void registerDefaultProperty(@NotNull String key);

	boolean isDefaultProperty(@NotNull String key);

	@NotNull PlayerPropertiesModel createModelWithDefaultProperties(@NotNull UUID playerId);

	@NotNull Set<String> cloneDefaultProperties();
}
