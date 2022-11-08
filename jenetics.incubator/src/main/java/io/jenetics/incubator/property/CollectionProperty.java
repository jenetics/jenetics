package io.jenetics.incubator.property;

import java.util.Collection;
import java.util.List;

public final class CollectionProperty extends IterableProperty {

	CollectionProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Collection<?> value
	) {
		super(enclosingObject, path, type, value, List.copyOf(value));
	}

	@Override
	public Collection<?> value() {
		return (Collection<?>)value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
