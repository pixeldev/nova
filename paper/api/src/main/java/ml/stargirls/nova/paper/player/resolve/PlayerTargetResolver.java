package ml.stargirls.nova.paper.player.resolve;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerTargetResolver {

	@Nullable String resolveIdStringSync(@NotNull CommandSender sender, @NotNull String targetName);

	@Nullable UUID resolveIdSync(@NotNull CommandSender sender, @NotNull String targetName);

	@Nullable String resolveNameSync(@NotNull CommandSender sender, @NotNull UUID targetId);
}
