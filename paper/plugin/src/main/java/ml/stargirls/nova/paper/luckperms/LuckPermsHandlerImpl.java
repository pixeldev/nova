package ml.stargirls.nova.paper.luckperms;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LuckPermsHandlerImpl
	implements LuckPermsHandler {

	@Override
	public @Nullable User getUser(@NotNull final UUID playerId) {
		return LuckPermsProvider.get()
			       .getUserManager()
			       .getUser(playerId);
	}

	@Override
	public @NotNull String getGroup(@NotNull final UUID playerId) {
		User user = getUser(playerId);

		if (user == null) {
			return "default";
		}

		return user.getPrimaryGroup();
	}
}
