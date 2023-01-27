package ml.stargirls.nova.paper.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
// avoid warnings since this class is auto-generated
@ConfigSerializable
public class Configuration {

	@NotNull private String clusterId = "cities";

	public String getClusterId() {
		return clusterId;
	}

	@NotNull private Teleport teleport = new Teleport();

	public Teleport getTeleport() {
		return teleport;
	}

	@NotNull private Chat chat = new Chat();

	public Chat getChat() {
		return chat;
	}

	@NotNull private Display display = new Display();

	public Display getDisplay() {
		return display;
	}

	@NotNull private Home home = new Home();

	public Home getHome() {
		return home;
	}

	@ConfigSerializable
	public static class Home {
		@NotNull private Map<String, Integer> maximum = Map.of("default", 3);

		public Map<String, Integer> getMaximum() {
			return maximum;
		}

		@NotNull private Map<String, Integer> maximumShared = Map.of("default", 3);

		public Map<String, Integer> getMaximumShared() {
			return maximumShared;
		}

		@NotNull private Pattern namePattern = Pattern.compile("[a-zA-Z0-9_]{3,16}");

		public Pattern getNamePattern() {
			return namePattern;
		}
	}

	@ConfigSerializable
	public static class Display {

		private int maxNickLength = 16;

		public int getMaxNickLength() {
			return maxNickLength;
		}

		private int minNickLength = 3;

		public int getMinNickLength() {
			return minNickLength;
		}

		private Pattern invalidPattern = Pattern.compile("[^a-zA-Z0-9_]");

		public Pattern getInvalidPattern() {
			return invalidPattern;
		}
	}

	@ConfigSerializable
	public static class Chat {

		@NotNull private Set<String> badWords = Set.of(
			"bad",
			"words"
		);

		public Set<String> getBadWords() {
			return badWords;
		}
	}

	@ConfigSerializable
	public static class Teleport {

		private int requestExpiration = 30;
		private int teleportDelay = 5;

		public int getRequestExpiration() {
			return requestExpiration;
		}

		public int getTeleportDelay() {
			return teleportDelay;
		}
	}
}
