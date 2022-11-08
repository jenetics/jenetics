package io.jenetics.incubator.property;

import java.util.Arrays;

public final class ArrayProperty extends IterableProperty {

	ArrayProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Object[] value
	) {
		super(enclosingObject, path, type, value, Arrays.asList(value));
	}

	@Override
	public Object[] value() {
		return (Object[])value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
