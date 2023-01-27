package ml.stargirls.nova.paper.player.connection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConnectionProcessRegistryImpl
	implements ConnectionProcessRegistry {

	private final List<ConnectionProcess> processes = new ArrayList<>();

	@Override
	public @NotNull List<ConnectionProcess> getAll() {
		return processes;
	}

	@Override
	public void register(@NotNull final ConnectionProcess process) {
		processes.add(process);
	}
}
