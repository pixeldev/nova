package ml.stargirls.nova.paper.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface BadWordChecker {

	boolean containsBadWordsSync(@NotNull String message, boolean replaceSymbols);

	boolean containsBadWordsSync(
		@NotNull Player sender,
		@NotNull String message,
		boolean replaceSymbols
	);

	@NotNull CompletableFuture<@NotNull Boolean> containsBadWords(
		@NotNull String message,
		boolean replaceSymbols
	);

	@NotNull CompletableFuture<@NotNull Boolean> containsBadWords(
		@NotNull Player sender,
		@NotNull String message,
		boolean replaceSymbols
	);
}
