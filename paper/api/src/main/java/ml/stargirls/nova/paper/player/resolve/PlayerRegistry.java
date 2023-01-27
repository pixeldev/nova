package ml.stargirls.nova.paper.player.resolve;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface PlayerRegistry {

	void registerSync(@NotNull Player player);

	void unregisterSync(@NotNull String playerId, @NotNull String playerName);

	@Nullable Set<@NotNull String> getAllNamesSync();

	@Nullable Set<@NotNull String> getAllIdsSync();

	@Nullable String getNameSync(@NotNull String playerId);

	@Nullable Collection<String> getNamesSync(@NotNull String... playerIds);

	@Nullable String getIdSync(@NotNull String playerName);

	@Nullable Collection<String> getIdsSync(boolean allowDuplicates, @NotNull String... playerNames);
}
