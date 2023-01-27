package ml.stargirls.nova.paper.player.data;

import ml.stargirls.maia.paper.codec.MinecraftModelReader;
import ml.stargirls.maia.paper.codec.MinecraftModelWriter;
import ml.stargirls.nova.paper.nms.NmsHelper;
import ml.stargirls.nova.paper.player.effect.PlayerEffect;
import ml.stargirls.storage.codec.ModelCodec;
import ml.stargirls.storage.model.Model;
import ml.stargirls.storage.util.Validate;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * This class represents all the data that is stored for a player to be loaded when they join or
 * switch servers.
 * <p>
 * NOTE: This class is not thread-safe and should not be accessed from multiple threads, also
 * shouldn't be cached. We strongly recommend to use {@link PlayerDataManager} to interact with this
 * class since it's handled by the plugin.
 */
public record PlayerDataModel(
	UUID playerId,
	double maxHealth, double health, int food,
	float saturation, int unsaturatedRegen,
	int saturatedRegen, float exhaustion, int starvation,
	int gameModeValue, float experience, int level,
	float walkSpeed, int fireTicks, float flySpeed,
	boolean flying, boolean invulnerable, boolean invisible,
	Collection<PlayerEffect> potionEffects
)
	implements Model {

	public static <Writer extends MinecraftModelWriter<Writer, ReadType>, ReadType> ReadType write(
		@NotNull final Writer writer,
		@NotNull final ModelCodec.Writer<PlayerEffect, ReadType> effectWriter,
		@NotNull final PlayerDataModel model
	) {
		return writer.writeNumber("maxHealth", model.maxHealth())
			       .writeNumber("health", model.health())
			       .writeNumber("food", model.food())
			       .writeNumber("saturation", model.saturation())
			       .writeNumber("unsaturatedRegen", model.unsaturatedRegen())
			       .writeNumber("saturatedRegen", model.saturatedRegen())
			       .writeNumber("exhaustion", model.exhaustion())
			       .writeNumber("starvation", model.starvation())
			       .writeNumber("gameModeValue", model.gameModeValue())
			       .writeNumber("experience", model.experience())
			       .writeNumber("level", model.level())
			       .writeNumber("walkSpeed", model.walkSpeed())
			       .writeNumber("fireTicks", model.fireTicks())
			       .writeNumber("flySpeed", model.flySpeed())
			       .writeBoolean("flying", model.flying())
			       .writeBoolean("invulnerable", model.invulnerable())
			       .writeBoolean("invisible", model.invisible())
			       .writeCollection("potionEffects", model.potionEffects(), effectWriter)
			       .end();
	}

	public static <Reader extends MinecraftModelReader<Reader, ReadType>, ReadType> PlayerDataModel read(
		@NotNull final Reader reader,
		@NotNull final ModelCodec.Reader<PlayerEffect, ReadType, Reader> effectReader,
		@NotNull final UUID playerId
	) {
		return new PlayerDataModel(
			playerId,
			Validate.notNull(reader.readNumber("maxHealth"), "maxHealth")
				.doubleValue(),
			Validate.notNull(reader.readNumber("health"), "health")
				.doubleValue(),
			Validate.notNull(reader.readNumber("food"), "food")
				.intValue(),
			Validate.notNull(reader.readNumber("saturation"), "saturation")
				.floatValue(),
			Validate.notNull(reader.readNumber("unsaturatedRegen"), "unsaturatedRegen")
				.intValue(),
			Validate.notNull(reader.readNumber("saturatedRegen"), "saturatedRegen")
				.intValue(),
			Validate.notNull(reader.readNumber("exhaustion"), "exhaustion")
				.floatValue(),
			Validate.notNull(reader.readNumber("starvation"), "starvation")
				.intValue(),
			Validate.notNull(reader.readNumber("gameModeValue"), "gameModeValue")
				.intValue(),
			Validate.notNull(reader.readNumber("experience"), "experience")
				.floatValue(),
			Validate.notNull(reader.readNumber("level"), "level")
				.intValue(),
			Validate.notNull(reader.readNumber("walkSpeed"), "walkSpeed")
				.floatValue(),
			Validate.notNull(reader.readNumber("fireTicks"), "fireTicks")
				.intValue(),
			Validate.notNull(reader.readNumber("flySpeed"), "flySpeed")
				.floatValue(),
			Validate.notNull(reader.readBoolean("flying"), "flying"),
			Validate.notNull(reader.readBoolean("invulnerable"), "invulnerable"),
			Validate.notNull(reader.readBoolean("invisible"), "invisible"),
			reader.readCollection("potionEffects", effectReader, HashSet::new)
		);
	}

	@SuppressWarnings("deprecation")
	@Contract(pure = true, value = "_ -> new")
	public static PlayerDataModel create(@NotNull final Player player) {
		AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

		return new PlayerDataModel(
			player.getUniqueId(),
			attributeInstance == null ?
			20 :
			attributeInstance.getValue(),
			player.getHealth(),
			player.getFoodLevel(),
			player.getSaturation(),
			player.getUnsaturatedRegenRate(),
			player.getSaturatedRegenRate(),
			player.getExhaustion(),
			player.getStarvationRate(),
			player
				.getGameMode()
				.getValue(),
			player.getExp(),
			player.getLevel(),
			player.getWalkSpeed(),
			player.getFireTicks(),
			player.getFlySpeed(),
			player.isFlying(),
			player.isInvulnerable(),
			player.isInvisible(),
			PlayerEffect.serializeEffects(player)
		);
	}

	@SuppressWarnings("deprecation")
	public void apply(@NotNull final Player player) {
		AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

		if (attributeInstance != null) {
			attributeInstance.setBaseValue(maxHealth);
		}

		player.setHealth(health);
		player.setFoodLevel(food);
		player.setSaturation(saturation);
		player.setUnsaturatedRegenRate(unsaturatedRegen);
		player.setSaturatedRegenRate(saturatedRegen);
		player.setExhaustion(exhaustion);
		player.setStarvationRate(starvation);

		GameMode gameMode = GameMode.getByValue(gameModeValue);

		if (gameMode != null) {
			player.setGameMode(gameMode);
		}

		player.setExp(experience);
		player.setLevel(level);
		player.setWalkSpeed(walkSpeed);
		player.setFireTicks(fireTicks);
		player.setFlySpeed(flySpeed);
		player.setFlying(flying);
		player.setInvulnerable(invulnerable);
		player.setInvisible(invisible);

		ServerPlayer serverPlayer = NmsHelper.unwrap(player);
		serverPlayer.removeAllEffects();

		potionEffects.forEach(effect -> effect.apply(player));
	}

	@Override
	public @NotNull String getId() {
		return playerId.toString();
	}
}
