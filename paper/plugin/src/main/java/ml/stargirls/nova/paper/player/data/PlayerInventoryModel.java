package ml.stargirls.nova.paper.player.data;

import ml.stargirls.maia.paper.codec.MinecraftModelReader;
import ml.stargirls.maia.paper.codec.MinecraftModelWriter;
import ml.stargirls.maia.paper.serialize.ItemSerialization;
import ml.stargirls.storage.model.Model;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

/**
 * This class represents all the player's inventory data that is stored for a player to be loaded
 * when they join or switch servers.
 * <p>
 * NOTE: This class is not thread-safe and should not be accessed from multiple threads, also
 * shouldn't be cached. We strongly recommend to use {@link PlayerDataManager} to interact with this
 * class since it's handled by the plugin.
 */
public record PlayerInventoryModel(
	@NotNull UUID playerId, @Nullable String inventory,
	@Nullable String enderChest
)
	implements Model {

	@Contract(pure = true, value = "_, _ -> param1")
	public static <ReadType> ReadType write(
		@NotNull
		final MinecraftModelWriter<? extends MinecraftModelWriter<?, ReadType>, ReadType> writer,
		@NotNull final PlayerInventoryModel model
	) {
		return writer.writeString("inventory", model.inventory())
			       .writeString("enderChest", model.enderChest())
			       .end();
	}

	@Contract(pure = true, value = "_, _ -> new")
	public static PlayerInventoryModel read(
		@NotNull final MinecraftModelReader<?, ?> reader,
		@NotNull final UUID playerId
	) {
		return new PlayerInventoryModel(
			playerId,
			reader.readString("inventory"),
			reader.readString("enderChest"));
	}

	@Contract(pure = true, value = "_ -> new")
	public static PlayerInventoryModel create(@NotNull final Player player) throws IOException {
		return new PlayerInventoryModel(
			player.getUniqueId(),
			ItemSerialization.serializeItems(player.getInventory()
				                                 .getContents()),
			ItemSerialization.serializeItems(player.getEnderChest()
				                                 .getStorageContents()));
	}

	/**
	 * Applies the inventory data to the player. Including its inventory and ender chest.
	 * <p>
	 * NOTE: This method will override the current player's inventory.
	 *
	 * @param player
	 * 	the player to apply the data to.
	 *
	 * @throws IOException
	 * 	if the data is invalid.
	 * @throws ClassNotFoundException
	 * 	if the data is invalid.
	 */
	@SuppressWarnings("ConstantConditions")
	public void apply(@NotNull final Player player) throws IOException, ClassNotFoundException {
		ItemStack[] inventory = ItemSerialization.deserializeItems(inventory());
		PlayerInventory playerInventory = player.getInventory();
		playerInventory.setContents(inventory);

		ItemStack[] enderChest = ItemSerialization.deserializeItems(enderChest());
		Inventory enderChestInventory = player.getEnderChest();
		enderChestInventory.setContents(enderChest);
	}

	@Override
	public @NotNull String getId() {
		return playerId.toString();
	}
}
