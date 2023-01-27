package ml.stargirls.nova.paper.player.permission;

import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public interface PermissionHelper {

	String PREFIX = "nova.";
	String[] BASE_PERMISSIONS = {
		PREFIX + "admin",
		PREFIX + "*"
	};

	static boolean hasPermission(
		@NotNull final Permissible permissible,
		@NotNull final String @NotNull ... permission
	) {
		if (permission.length == 0) {
			return true;
		}

		if (permissible.isOp()) {
			return true;
		}

		for (String perm : permission) {
			if (permissible.hasPermission(perm)) {
				return true;
			}
		}

		for (String basePermission : BASE_PERMISSIONS) {
			if (permissible.hasPermission(basePermission)) {
				return true;
			}
		}

		return false;
	}
}
