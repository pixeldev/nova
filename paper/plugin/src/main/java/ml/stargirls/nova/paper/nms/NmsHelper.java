package ml.stargirls.nova.paper.nms;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NmsHelper {

	static @NotNull ServerPlayer unwrap(@NotNull final Player player) {
		return ((CraftPlayer) player).getHandle();
	}

	static @NotNull Player wrap(@NotNull final ServerPlayer player) {
		return player.getBukkitEntity();
	}
}
