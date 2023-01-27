package ml.stargirls.nova.paper.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentMatcherImpl
	implements ComponentMatcher {

	@Override
	public @Nullable String match(@NotNull final Component where, @NotNull final String input) {
		String displayNamePlain = PlainTextComponentSerializer.plainText()
			                          .serialize(where);

		if (!displayNamePlain.equalsIgnoreCase(input)) {
			displayNamePlain = null;
		}

		for (Component child : where.children()) {
			String childPlain = PlainTextComponentSerializer.plainText()
				                    .serialize(child);

			if (childPlain.equalsIgnoreCase(input)) {
				displayNamePlain = childPlain;
			}
		}

		return displayNamePlain;
	}
}
