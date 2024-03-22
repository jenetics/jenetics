package io.jenetics.incubator.beans.property;

import static java.util.Objects.requireNonNull;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.description.Getter;
import io.jenetics.incubator.beans.description.Setter;

record PropParam(
	Path path,
	Object enclosure,
	Object value,
	Class<?> type,
	Getter getter,
	Setter setter
) {

	PropParam {
		requireNonNull(path);
		requireNonNull(enclosure);
		requireNonNull(type);
		requireNonNull(getter);
	}

}
