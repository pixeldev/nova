package ml.stargirls.nova.paper.location;

import org.jetbrains.annotations.NotNull;

public record ServerLocationModel(
	@NotNull String serverId,
	@NotNull LocationModel location
) {

	@Override
	public String toString() {
		return "ServerLocationModel{" + "serverId='" + serverId + '\'' + ", location=" + location + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ServerLocationModel that = (ServerLocationModel) o;

		if (!serverId.equals(that.serverId)) {
			return false;
		}
		return location.equals(that.location);
	}

	@Override
	public int hashCode() {
		int result = serverId.hashCode();
		result = 31 * result + location.hashCode();
		return result;
	}
}
