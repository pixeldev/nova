package ml.stargirls.nova.paper.player.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerResolverImpl
	implements PlayerResolver {

	@Inject private PlayerRegistry playerRegistry;

	@Override
	public @Nullable String resolveIdStringSync(@NotNull final String name) {
		return playerRegistry.getIdSync(name);
	}

	@Override
	public @Nullable UUID resolveIdSync(@NotNull final String name) {
		String playerId = resolveIdStringSync(name);

		if (playerId == null) {
			return null;
		}

		return UUID.fromString(playerId);
	}

	@Override
	public @Nullable Collection<UUID> resolveIdsSync(
		final boolean allowDuplicates,
		final String... names
	) {
		int namesLength = names.length;

		if (namesLength == 0) {
			return null;
		}

		Collection<String> playerIds = playerRegistry.getIdsSync(allowDuplicates, names);

		if (playerIds == null || playerIds.isEmpty()) {
			return null;
		}

		List<UUID> uuids = new ArrayList<>(playerIds.size());

		for (String playerId : playerIds) {
			if (playerId == null) {
				continue;
			}
			uuids.add(UUID.fromString(playerId));
		}

		return uuids;
	}

	@Override
	public @Nullable String resolveNameSync(@NotNull final UUID id) {
		return playerRegistry.getNameSync(id.toString());
	}

	@Override
	public @Nullable Collection<String> resolveNamesSync(final UUID... ids) {
		String[] playerIds = new String[ids.length];

		for (int i = 0; i < ids.length; i++) {
			playerIds[i] = ids[i].toString();
		}

		return playerRegistry.getNamesSync(playerIds);
	}
}
