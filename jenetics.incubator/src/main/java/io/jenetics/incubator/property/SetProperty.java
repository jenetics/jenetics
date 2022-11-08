package io.jenetics.incubator.property;

import java.util.List;
import java.util.Set;

public final class SetProperty extends IterableProperty {

	SetProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Set<?> value
	) {
		super(enclosingObject, path, type, value, List.copyOf(value));
	}

	@Override
	public Set<?> value() {
		return (Set<?>)value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
