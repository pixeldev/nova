package ml.stargirls.nova.paper.chat;

import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.player.permission.PermissionHelper;
import org.ahocorasick.trie.Trie;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BadWordCheckerImpl
	implements BadWordChecker {

	private static final Pattern SYMBOLS =
		Pattern.compile("[-!$%^&*@()_+|~=`{}\\[\\]:\";'<>?,./\\\\]");

	@Inject private Trie trie;
	@Inject private MessageHandler messageHandler;
	@Inject private Executor executor;

	@Override
	public boolean containsBadWordsSync(
		@NotNull final String message,
		final boolean replaceSymbols
	) {
		String formattedMessage = message;

		if (replaceSymbols) {
			Matcher symbolMatcher = SYMBOLS.matcher(message);
			formattedMessage = symbolMatcher.replaceAll("");
		}

		return trie.containsMatch(formattedMessage);
	}

	@Override
	public boolean containsBadWordsSync(
		@NotNull final Player sender,
		@NotNull final String message,
		final boolean replaceSymbols
	) {
		if (PermissionHelper.hasPermission(sender, "badwords.bypass")) {
			return false;
		}

		boolean containsBadWords = containsBadWordsSync(message, replaceSymbols);

		if (containsBadWords) {
			messageHandler.send(sender, "bad-words");
		}

		return containsBadWords;
	}

	@Override
	public @NotNull CompletableFuture<@NotNull Boolean> containsBadWords(
		@NotNull final String message,
		final boolean replaceSymbols
	) {
		return CompletableFuture.supplyAsync(
			() -> containsBadWordsSync(message, replaceSymbols),
			executor);
	}

	@Override
	public @NotNull CompletableFuture<@NotNull Boolean> containsBadWords(
		@NotNull final Player sender,
		@NotNull final String message,
		final boolean replaceSymbols
	) {
		return CompletableFuture.supplyAsync(
			() -> containsBadWordsSync(sender, message, replaceSymbols),
			executor);
	}
}
