package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import io.jenetics.incubator.property.Property.Path;

public record DataObject(Path path, Object value) {
	public DataObject {
		requireNonNull(path);
	}

	public DataObject(Object value) {
		this(Path.EMPTY, value);
	}
}
