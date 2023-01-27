package ml.stargirls.nova.paper.player.property;

import ml.stargirls.storage.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public record PlayerPropertiesModel(@NotNull UUID uuid, @NotNull Set<String> properties)
	implements Model {

	public PlayerPropertiesModel(
		@NotNull final UUID uuid,
		@NotNull final Set<String> properties
	) {
		this.uuid = uuid;
		this.properties = properties;
	}

	@Override
	public @NotNull String getId() {
		return uuid.toString();
	}

	public boolean hasProperty(@NotNull final String property) {
		return properties.contains(property);
	}

	public void setProperty(@NotNull final String property) {
		properties.add(property);
	}

	public boolean toggleProperty(@NotNull final String property) {
		if (properties.remove(property)) {
			return false;
		}

		return properties.add(property);
	}
}
