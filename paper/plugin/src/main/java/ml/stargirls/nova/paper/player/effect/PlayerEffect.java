package ml.stargirls.nova.paper.player.effect;

import ml.stargirls.nova.paper.nms.NmsHelper;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public record PlayerEffect(int effectId, int amplifier, int duration) {

	@Contract(pure = true, value = "_ -> new")
	public static Collection<PlayerEffect> serializeEffects(@NotNull final Player player) {
		return NmsHelper.unwrap(player)
			       .getActiveEffects()
			       .stream()
			       .map(effectInstance -> new PlayerEffect(
				       MobEffect.getId(effectInstance.getEffect()),
				       effectInstance.getAmplifier(),
				       effectInstance.getDuration()
			       ))
			       .collect(Collectors.toList());
	}

	@SuppressWarnings("deprecation")
	public void apply(@NotNull final Player player) {
		PotionEffectType effectType = PotionEffectType.getById(effectId);

		if (effectType == null) {
			return;
		}

		player.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PlayerEffect that = (PlayerEffect) o;

		if (effectId != that.effectId) {
			return false;
		}
		if (amplifier != that.amplifier) {
			return false;
		}
		return duration == that.duration;
	}

	@Override
	public int hashCode() {
		int result = effectId;
		result = 31 * result + amplifier;
		result = 31 * result + duration;
		return result;
	}
}
