package ml.stargirls.nova.paper.component;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentMatcher {

	@Nullable String match(@NotNull Component where, @NotNull String input);
}
