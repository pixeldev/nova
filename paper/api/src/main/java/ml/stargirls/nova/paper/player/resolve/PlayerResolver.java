package ml.stargirls.nova.paper.player.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PlayerResolver {

	@Nullable String resolveIdStringSync(@NotNull String name);

	@Nullable UUID resolveIdSync(@NotNull String name);

	@Nullable Collection<@NotNull UUID> resolveIdsSync(
		boolean allowDuplicates,
		@NotNull String... names
	);

	@Nullable String resolveNameSync(@NotNull UUID id);

	@Nullable Collection<String> resolveNamesSync(@NotNull UUID... ids);
}
