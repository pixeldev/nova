package ml.stargirls.nova.paper.player.resolve;

import ml.stargirls.nova.paper.config.Configuration;
import ml.stargirls.storage.redis.connection.JedisInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class RedisPlayerRegistry
	implements PlayerRegistry {

	private final String UUID_TO_NAME_KEY;
	private final String NAME_TO_UUID_KEY;

	private final JedisPool jedisPool;
	private final PlayerNameSanitizer nameSanitizer;

	@Inject
	public RedisPlayerRegistry(
		@NotNull final JedisInstance jedisInstance,
		@NotNull final Configuration configuration,
		@NotNull final PlayerNameSanitizer nameSanitizer
	) {
		String clusterId = configuration.getClusterId();
		this.UUID_TO_NAME_KEY = "nova:" + clusterId + ":uuid-to-name";
		this.NAME_TO_UUID_KEY = "nova:" + clusterId + ":name-to-uuid";

		this.jedisPool = jedisInstance.jedisPool();
		this.nameSanitizer = nameSanitizer;
	}

	@Override
	public void registerSync(@NotNull final Player player) {
		String playerId = player.getUniqueId()
			                  .toString();
		String playerName = nameSanitizer.sanitize(player.getName());

		try (Jedis jedis = jedisPool.getResource()) {
			jedis.hset(UUID_TO_NAME_KEY, playerId, playerName);
			jedis.hset(NAME_TO_UUID_KEY, playerName, playerId);
		}
	}

	@Override
	public void unregisterSync(
		@NotNull final String playerId,
		@NotNull final String playerName
	) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.hdel(UUID_TO_NAME_KEY, playerId);
			jedis.hdel(NAME_TO_UUID_KEY, playerName);
		}
	}

	@Override
	public @Nullable Set<@NotNull String> getAllNamesSync() {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hkeys(NAME_TO_UUID_KEY);
		}
	}

	@Override
	public @Nullable Set<@NotNull String> getAllIdsSync() {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hkeys(UUID_TO_NAME_KEY);
		}
	}

	@Override
	public @Nullable String getNameSync(@NotNull final String playerId) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hget(UUID_TO_NAME_KEY, playerId);
		}
	}

	@Override
	public @Nullable Collection<String> getNamesSync(
		@NotNull final String... playerIds
	) {
		if (playerIds.length == 0) {
			return Collections.emptyList();
		}

		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hmget(UUID_TO_NAME_KEY, playerIds);
		}
	}

	@Override
	public @Nullable String getIdSync(@NotNull final String playerName) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hget(NAME_TO_UUID_KEY, nameSanitizer.sanitize(playerName));
		}
	}

	@Override
	public @Nullable Collection<String> getIdsSync(
		final boolean allowDuplicates,
		@NotNull final String... playerNames
	) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hmget(NAME_TO_UUID_KEY, nameSanitizer.sanitize(allowDuplicates, playerNames));
		}
	}
}
