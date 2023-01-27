package ml.stargirls.nova.paper.player.home;

import ml.stargirls.nova.paper.home.HomeModel;
import ml.stargirls.storage.model.Model;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerHomeModel
	implements Model {

	private final UUID uuid;
	private final Set<String> homeNames;

	/**
	 * we use a map to store the shared homes because we need to know the owner of the home
	 */
	private final Map<String, Shared> sharedHomes;

	private int maxHomes;
	private int maxSharedHomes;

	public PlayerHomeModel(
		@NotNull final UUID uuid,
		@NotNull final Set<String> homeNames,
		@NotNull final Map<String, Shared> sharedHomes,
		final int maxHomes,
		final int maxSharedHomes
	) {
		this.uuid = uuid;
		this.homeNames = homeNames;
		this.sharedHomes = sharedHomes;
		this.maxHomes = maxHomes;
		this.maxSharedHomes = maxSharedHomes;
	}

	@Override
	public @NotNull String getId() {
		return uuid.toString();
	}

	public @NotNull UUID getUuid() {
		return uuid;
	}

	public @NotNull Component generateAllHomeNames(@NotNull final Component separator) {
		TextComponent.Builder homes = Component.text();
		int totalHomes = homeNames.size() + sharedHomes.size();
		int i = 0;

		for (String home : homeNames) {
			homes.append(Component.text(home));

			if (i++ < totalHomes - 1) {
				homes.append(separator);
			}
		}

		for (String home : sharedHomes.keySet()) {
			homes.append(Component.text(home));

			if (i++ < totalHomes - 1) {
				homes.append(separator);
			}
		}

		return homes.build();
	}

	public @NotNull String formatHomeId(@NotNull final String homeName) {
		return HomeModel.ID_FORMAT.formatted(uuid.toString(), homeName.toLowerCase(Locale.ROOT));
	}

	public @Nullable String resolveHomeId(@NotNull final String homeName) {
		String homeId = homeName.toLowerCase(Locale.ROOT);

		if (homeNames.contains(homeId)) {
			return HomeModel.ID_FORMAT.formatted(uuid.toString(), homeId);
		}

		Shared shared = sharedHomes.get(homeId);

		if (shared == null) {
			return null;
		}

		return shared.realId;
	}

	public @NotNull Set<String> getHomeNames() {
		return homeNames;
	}

	public boolean addHome(@NotNull final String name) {
		return homeNames.add(name);
	}

	protected @NotNull Map<String, Shared> getSharedHomes() {
		return sharedHomes;
	}

	public @NotNull Set<String> getSharedHomeNames() {
		return sharedHomes.keySet();
	}

	public @Nullable String getSharedHomeId(@NotNull final String alias) {
		Shared shared = sharedHomes.get(alias);
		return shared == null ?
		       null :
		       shared.realId;
	}

	public boolean addSharedHome(@NotNull final String alias, @NotNull final String realId) {
		if (sharedHomes.containsKey(alias)) {
			return false;
		}

		return sharedHomes.put(alias, new Shared(alias, realId)) == null;
	}

	public boolean removeSharedHome(@NotNull final String alias) {
		return sharedHomes.remove(alias) != null;
	}

	public int getMaxHomes() {
		return maxHomes;
	}

	public void setMaxHomes(final int maxHomes) {
		this.maxHomes = maxHomes;
	}

	public void incrementMaxHomes() {
		maxHomes++;
	}

	public void decrementMaxHomes() {
		maxHomes--;
	}

	public int getMaxSharedHomes() {
		return maxSharedHomes;
	}

	public void setMaxSharedHomes(final int maxSharedHomes) {
		this.maxSharedHomes = maxSharedHomes;
	}

	public void incrementMaxSharedHomes() {
		maxSharedHomes++;
	}

	public void decrementMaxSharedHomes() {
		maxSharedHomes--;
	}

	public record Shared(@NotNull String alias, @NotNull String realId) { }
}
