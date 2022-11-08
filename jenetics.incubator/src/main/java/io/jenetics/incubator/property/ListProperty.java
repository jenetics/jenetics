package io.jenetics.incubator.property;

import java.util.List;

public final class ListProperty extends IterableProperty {

	@SuppressWarnings("unchecked")
	ListProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final List<?> value
	) {
		super(enclosingObject, path, type, value, (List<Object>)value);
	}

	@Override
	public List<?> value() {
		return (List<?>)value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
