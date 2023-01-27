package ml.stargirls.nova.paper.player.connection;

import org.jetbrains.annotations.NotNull;

public interface ConnectionProcess {

	/**
	 * Process the player's disconnect. This method is called from an async thread so, it is not safe
	 * to call Bukkit methods.
	 *
	 * @param context
	 * 	The context of this connection.
	 */
	default void processDisconnect(@NotNull ConnectionContext context)
		throws Exception {

	}

	/**
	 * Process the player's switch server. This method is called from an async thread so, it's not
	 * safe to call Bukkit methods.
	 *
	 * @param context
	 * 	The context of this connection.
	 * @param destination
	 * 	The destination server.
	 *
	 * @throws Exception
	 * 	If an error occurs.
	 */
	default void processSwitchServer(@NotNull ConnectionContext context, @NotNull String destination)
		throws Exception {

	}

	/**
	 * Process the player's connect. This method is called from an async thread so, it is not safe to
	 * call Bukkit methods.
	 *
	 * @param context
	 * 	The context of this connection.
	 */
	default void processConnect(@NotNull ConnectionContext context)
		throws Exception {

	}
}
