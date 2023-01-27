package ml.stargirls.nova.paper.player.connection;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConnectionProcessRegistry {

	@NotNull List<@NotNull ConnectionProcess> getAll();

	void register(@NotNull ConnectionProcess process);
}
