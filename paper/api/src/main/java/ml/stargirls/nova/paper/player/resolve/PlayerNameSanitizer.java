package ml.stargirls.nova.paper.player.resolve;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PlayerNameSanitizer {

	@NotNull String sanitize(@NotNull String username);

	@NotNull String[] sanitize(boolean allowDuplicates, @NotNull String... usernames);

	@NotNull Collection<String> sanitize(
		boolean allowDuplicates,
		@NotNull Collection<String> usernames
	);
}
