package ml.stargirls.nova.paper.player.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public interface PlayerServerService {

	/**
	 * Fetches all the {@link PlayerServerModel}s from the redis cache and returns them as a
	 * {@link CompletableFuture}. NOTE: This method will return an empty list if no data is found.
	 *
	 * @param usernames
	 * 	The usernames to fetch data for.
	 *
	 * @return A {@link CompletableFuture} containing a list of {@link PlayerServerModel}s.
	 */
	default @NotNull CompletableFuture<@Nullable Queue<@NotNull PlayerServerModel>> query(
		boolean allowDuplicates,
		@NotNull String... usernames
	) {
		return query(
			allowDuplicates,
			false,
			usernames
		);
	}

	/**
	 * Fetches all the {@link PlayerServerModel}s from the redis cache and returns them as a
	 * {@link CompletableFuture}. NOTE: This method will return an empty list if no data is found.
	 *
	 * @param usernames
	 * 	The usernames to fetch data for.
	 * @param requireNonNull
	 * 	If failFast is true and a username is not found, the future will complete exceptionally.
	 *
	 * @return A {@link CompletableFuture} containing a list of {@link PlayerServerModel}s.
	 */
	@NotNull CompletableFuture<@Nullable Queue<@Nullable PlayerServerModel>> query(
		boolean allowDuplicates,
		boolean requireNonNull,
		@NotNull String... usernames
	);

	/**
	 * Fetches a {@link PlayerServerModel} from the redis cache.
	 *
	 * @param username
	 * 	The username to fetch data for.
	 *
	 * @return A {@link CompletableFuture} containing a {@link PlayerServerModel}.
	 */
	@NotNull CompletableFuture<@Nullable PlayerServerModel> find(@NotNull String username);

	@Nullable PlayerServerModel findSync(@NotNull String username);
}
