package ml.stargirls.nova.paper.player.data;

import org.jetbrains.annotations.Nullable;

public record FullPlayerData(
	@Nullable PlayerDataModel playerDataModel,
	@Nullable PlayerInventoryModel playerInventoryModel
) {
}
