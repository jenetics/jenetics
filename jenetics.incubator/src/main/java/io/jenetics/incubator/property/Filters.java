package io.jenetics.incubator.property;

import java.util.function.Predicate;
import java.util.regex.Pattern;

final class Filters {
	private Filters() {
	}

	static Pattern toPattern(final String glob) {
		return Pattern.compile(
			"^" +
				Pattern.quote(glob)
					.replace("*", "\\E.*\\Q")
					.replace("?", "\\E.\\Q") +
				"$"
		);
	}

	static Predicate<DataObject> toFilter(final Pattern pattern) {
		return object -> pattern
			.matcher(object.value().getClass().getName())
			.matches();
	}

}
