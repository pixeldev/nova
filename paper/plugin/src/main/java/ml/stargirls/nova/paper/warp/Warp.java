package ml.stargirls.nova.paper.warp;

import ml.stargirls.nova.paper.location.ServerLocationModel;
import ml.stargirls.storage.model.Model;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class Warp
	implements Model {

	private final String id;
	private boolean restricted;
	private boolean listed;
	private Component displayName;
	private ServerLocationModel location;

	public Warp(
		@NotNull final String id,
		final boolean restricted,
		final boolean listed,
		@NotNull final Component displayName,
		@NotNull final ServerLocationModel location
	) {
		this.id = id;
		this.restricted = restricted;
		this.listed = listed;
		this.displayName = displayName;
		this.location = location;
	}

	@Override
	public @NotNull String getId() {
		return id;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(final boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isListed() {
		return listed;
	}

	public void setListed(final boolean listed) {
		this.listed = listed;
	}

	public @NotNull Component getDisplayName() {
		return displayName;
	}

	public void setDisplayName(@NotNull final Component displayName) {
		this.displayName = displayName;
	}

	public @NotNull ServerLocationModel getLocation() {
		return location;
	}

	public void setLocation(@NotNull final ServerLocationModel location) {
		this.location = location;
	}
}
