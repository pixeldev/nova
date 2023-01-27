package ml.stargirls.nova.paper.chat.mention;

import ml.stargirls.nova.paper.player.identity.PlayerDisplayIdentityModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface MentionManager {

	@Nullable Collection<UUID> getMentionedPlayers(@NotNull String message);

	void detectAndNotify(
		@NotNull String message,
		@NotNull PlayerDisplayIdentityModel senderDisplayIdentity
	);
}
