package ml.stargirls.nova.paper.player.resolve;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PlayerNameSanitizerImpl
	implements PlayerNameSanitizer {

	@Override
	public @NotNull String sanitize(@NotNull final String username) {
		return username.toLowerCase(Locale.ROOT);
	}

	@Override
	public @NotNull String[] sanitize(
		final boolean allowDuplicates,
		@NotNull final String... usernames
	) {
		int namesLength = usernames.length;
		String[] formattedNames;

		if (allowDuplicates) {
			formattedNames = new String[namesLength];

			for (int i = 0; i < namesLength; i++) {
				formattedNames[i] = sanitize(usernames[i]);
			}
		} else {
			Set<String> formattedNamesSet = new HashSet<>(namesLength);

			for (final String name : usernames) {
				formattedNamesSet.add(sanitize(name));
			}

			formattedNames = formattedNamesSet.toArray(new String[0]);
		}

		return formattedNames;
	}

	@Override
	public @NotNull Collection<String> sanitize(
		final boolean allowDuplicates,
		@NotNull final Collection<String> usernames
	) {
		int namesLength = usernames.size();
		Collection<String> formattedNames;

		if (allowDuplicates) {
			formattedNames = new ArrayList<>(namesLength);

			for (String username : usernames) {
				formattedNames.add(sanitize(username));
			}
		} else {
			formattedNames = new HashSet<>(namesLength);

			for (final String name : usernames) {
				formattedNames.add(sanitize(name));
			}
		}

		return formattedNames;
	}
}
