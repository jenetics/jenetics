package io.jenetics.incubator.property;

import static java.util.Collections.emptyIterator;

import java.util.Arrays;
import java.util.Iterator;

public final class ArrayProperty extends IterableProperty {

	ArrayProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject, path, value);
	}

	@Override
	public Object[] value() {
		return (Object[])value;
	}

	public int size() {
		return value != null ? value().length : 0;
	}

	public Object get(final int index) {
		if (value == null) {
			throw new IndexOutOfBoundsException("Array is null.");
		}

		return value()[index];
	}

	@Override
	public Iterator<Object> iterator() {
		return value != null
			? Arrays.asList(value()).iterator()
			: emptyIterator();
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
