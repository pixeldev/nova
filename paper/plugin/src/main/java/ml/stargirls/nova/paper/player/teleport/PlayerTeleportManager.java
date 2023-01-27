package ml.stargirls.nova.paper.player.teleport;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.nova.paper.location.LocationModel;
import ml.stargirls.nova.paper.location.LocationModelCodec;
import ml.stargirls.storage.gson.codec.JsonWriter;
import ml.stargirls.storage.redis.connection.JedisInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import java.util.UUID;

public class PlayerTeleportManager {

	@Inject private Gson gson;

	private final JedisPool jedisPool;

	@Inject
	public PlayerTeleportManager(@NotNull final JedisInstance jedisInstance) {
		this.jedisPool = jedisInstance.jedisPool();
	}

	public void registerPendingTeleport(@NotNull final PendingTeleportModel model) {
		try (Jedis jedis = jedisPool.getResource()) {
			String name = model.playerName();

			JsonObject locationModelJson =
				JsonWriter
					.create()
					.writeObject("location", model.locationModel(), LocationModelCodec.JSON_WRITER)
					.writeDetailedUuid("targetId", model.targetId())
					.end();

			String key = "nova:pending-teleport:" + name;
			jedis.set(key, gson.toJson(locationModelJson));
			jedis.expire(key, 60);
		}
	}

	public @Nullable PendingTeleportModel invalidatePendingTeleport(@NotNull final Player player) {
		try (Jedis jedis = jedisPool.getResource()) {
			String name = player.getName();
			String key = "nova:pending-teleport:" + name;
			String locationModelJson = jedis.get(key);

			if (locationModelJson == null) {
				return null;
			}

			jedis.del(key);

			JsonObject locationModelJsonObject = gson.fromJson(locationModelJson, JsonObject.class);
			MinecraftJsonReader reader = MinecraftJsonReader.create(locationModelJsonObject);

			UUID targetId = reader.readDetailedUuid("targetId");
			LocationModel locationModel = reader.readObject("location", LocationModelCodec.JSON_READER);
			return new PendingTeleportModel(name, targetId, locationModel);
		}
	}

	public void deletePendingTeleport(@NotNull final String playerName) {
		try (Jedis jedis = jedisPool.getResource()) {
			String key = "nova:pending-teleport:" + playerName;
			jedis.del(key);
		}
	}
}
