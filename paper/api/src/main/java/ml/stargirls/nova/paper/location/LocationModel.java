package ml.stargirls.nova.paper.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class LocationModel {

	private final String worldName;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	private LocationModel(
		@NotNull final String worldName,
		final double x,
		final double y,
		final double z,
		final float yaw,
		final float pitch
	) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public static @NotNull LocationModel create(
		@NotNull final String worldName,
		final double x,
		final double y,
		final double z,
		final float yaw,
		final float pitch
	) {
		return new LocationModel(worldName, x, y, z, yaw, pitch);
	}

	/**
	 * @param location
	 *  {@link Location} to convert
	 *
	 * @return {@link LocationModel} from {@link Location}
	 *
	 * @throws IllegalArgumentException
	 * 	if {@link Location#getWorld()} is null
	 */
	public static @NotNull LocationModel fromLocation(@NotNull final Location location) {
		World world = location.getWorld();

		if (world == null) {
			throw new IllegalArgumentException("World is null");
		}

		return new LocationModel(
			world.getName(),
			location.getX(),
			location.getY(),
			location.getZ(),
			location.getYaw(),
			location.getPitch()
		);
	}

	/**
	 * Create {@link LocationModel} from {@link Location} using block coordinates to center the
	 * location
	 *
	 * @param location
	 *  {@link Location} to convert
	 *
	 * @return {@link LocationModel} from {@link Location}
	 *
	 * @throws IllegalArgumentException
	 * 	if {@link Location#getWorld()} is null
	 */
	public static @NotNull LocationModel centered(@NotNull final Location location) {
		World world = location.getWorld();

		if (world == null) {
			throw new IllegalArgumentException("World is null");
		}

		return new LocationModel(
			world.getName(),
			location.getBlockX() + 0.5,
			location.getBlockY() + 0.5,
			location.getBlockZ() + 0.5,
			location.getYaw(),
			location.getPitch()
		);
	}

	public @NotNull Location toLocation() throws IllegalArgumentException {
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			throw new IllegalArgumentException("World is null");
		}
		return new Location(world, x, y, z, yaw, pitch);
	}

	public @NotNull World getWorld() {
		World world = Bukkit.getWorld(worldName);

		if (world == null) {
			throw new IllegalArgumentException("World " + worldName + " not found");
		}

		return world;
	}

	public @NotNull String getWorldName() {
		return worldName;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	@Override
	public String toString() {
		return "Location{" + "world=" + worldName +
		       ",x=" + x + ",y=" + y + ",z=" + z +
		       ",pitch=" + pitch + ",yaw=" + yaw + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		LocationModel that = (LocationModel) o;

		if (Double.compare(that.x, x) != 0) {
			return false;
		}
		if (Double.compare(that.y, y) != 0) {
			return false;
		}
		if (Double.compare(that.z, z) != 0) {
			return false;
		}
		if (Float.compare(that.yaw, yaw) != 0) {
			return false;
		}
		if (Float.compare(that.pitch, pitch) != 0) {
			return false;
		}
		return worldName.equals(that.worldName);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + worldName.hashCode();
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^
		                          (Double.doubleToLongBits(this.x) >>>
		                           32));
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^
		                          (Double.doubleToLongBits(this.y) >>>
		                           32));
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^
		                          (Double.doubleToLongBits(this.z) >>>
		                           32));
		hash = 19 * hash + Float.floatToIntBits(this.pitch);
		hash = 19 * hash + Float.floatToIntBits(this.yaw);
		return hash;
	}
}
