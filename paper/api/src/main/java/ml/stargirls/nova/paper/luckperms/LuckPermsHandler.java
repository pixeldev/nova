package ml.stargirls.nova.paper.luckperms;

import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface LuckPermsHandler {

	@Nullable User getUser(@NotNull UUID playerId);

	@NotNull String getGroup(@NotNull UUID playerId);

}
