package io.jenetics.incubator.property;

import static java.lang.String.format;

final class Properties {
	private Properties() {
	}

	static String toString(final String name, final Property property) {
		return format(
			"%s[path=%s, value=%s, type=%s, enclosingType=%s]",
			name,
			property.path(),
			property.value(),
			property.type() != null ? property.type().getName() : null,
			property.enclosingObject().getClass().getName()
		);
	}

}
