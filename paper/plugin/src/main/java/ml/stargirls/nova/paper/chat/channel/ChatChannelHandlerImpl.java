package ml.stargirls.nova.paper.chat.channel;

import ml.stargirls.maia.paper.translation.SendingModes;
import ml.stargirls.message.MessageHandler;
import ml.stargirls.nova.paper.chat.channel.player.PlayerChannelModel;
import ml.stargirls.storage.dist.CachedRemoteModelService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.UUID;

public class ChatChannelHandlerImpl
	implements ChatChannelHandler {

	@Inject private CachedRemoteModelService<PlayerChannelModel> playerChannelModelService;
	@Inject private MessageHandler messageHandler;
	@Inject private Logger logger;

	@Override
	public void joinToChannel(@NotNull final Player player, @NotNull final ChatChannel channel) {
		UUID playerId = player.getUniqueId();
		PlayerChannelModel model = playerChannelModelService.getSync(playerId.toString());
		joinToChannel(player, model, channel);
	}

	@Override
	public void toggleChannel(@NotNull final Player player, @NotNull final ChatChannel channel) {
		UUID playerId = player.getUniqueId();
		PlayerChannelModel model = playerChannelModelService.getSync(playerId.toString());

		if (model == null) {
			joinToChannel(player, null, channel);
		} else {
			String currentChannelId = model.getChannelId();

			if (currentChannelId != null && currentChannelId.equals(channel.getId())) {
				exitFromChannel(player, model);
			} else {
				joinToChannel(player, model, channel);
			}
		}
	}

	@Override
	public void exitFromChannel(@NotNull final Player player) {
		UUID playerId = player.getUniqueId();
		PlayerChannelModel model = playerChannelModelService.getSync(playerId.toString());

		if (model == null || model.getChannelId() == null) {
			messageHandler.sendIn(player, SendingModes.ERROR, "chat.channel.not-in");
			return;
		}

		exitFromChannel(player, model);
	}

	private void joinToChannel(
		@NotNull final Player player,
		@Nullable final PlayerChannelModel model,
		@NotNull final ChatChannel channel
	) {
		String channelId = channel.getId();
		PlayerChannelModel savedModel;

		if (model == null) {
			savedModel = PlayerChannelModel.create(player.getUniqueId(), channelId);
		} else {
			savedModel = model;
			String oldChannelId = model.getChannelId();

			if (oldChannelId != null && oldChannelId.equals(channelId)) {
				messageHandler.sendIn(player, SendingModes.ERROR, "chat.channel.already-in");
				return;
			}

			model.setChannelId(channelId);
		}

		playerChannelModelService.save(savedModel)
			.whenComplete((result, throwable) -> {
				if (throwable != null) {
					messageHandler.sendIn(player, SendingModes.ERROR, "chat.channel.join-failed");
					logger.error("Failed to save player channel model", throwable);
					return;
				}

				Component channelName = messageHandler.get(player, "chat.channel." + channelId + ".name");
				messageHandler.sendReplacingIn(
					player,
					SendingModes.PING,
					"chat.channel.join-success",
					Placeholder.component("channel", channelName));
			});
	}

	private void exitFromChannel(
		@NotNull final Player player,
		@NotNull final PlayerChannelModel model
	) {
		model.setChannelId(null);
		playerChannelModelService.save(model)
			.whenComplete((result, throwable) -> {
				if (throwable != null) {
					messageHandler.sendIn(player, SendingModes.ERROR, "chat.channel.exit-failed");
					logger.error("Failed to save player channel model", throwable);
					return;
				}

				messageHandler.sendIn(player, SendingModes.PING, "chat.channel.exit-success");
			});
	}
}
