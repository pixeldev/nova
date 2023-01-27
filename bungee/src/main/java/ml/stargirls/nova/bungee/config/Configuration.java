package ml.stargirls.nova.bungee.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@SuppressWarnings("ALL") // avoid warnings since this class is auto-generated
@ConfigSerializable
public class Configuration {

	private Redis redis = new Redis();

	public Redis redis() {
		return redis;
	}

	private int threads = 4;

	public int threads() {
		return threads;
	}

	@ConfigSerializable
	public static class Redis {

		@NotNull private String host = "localhost";
		@NotNull private String password = "";
		private int port = 6379;
		private int timeout = 2000;

		public @NotNull String host() {
			return host;
		}

		public @NotNull String password() {
			return password;
		}

		public int port() {
			return port;
		}

		public int timeout() {
			return timeout;
		}
	}
}
