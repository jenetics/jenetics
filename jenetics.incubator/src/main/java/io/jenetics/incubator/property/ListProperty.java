package io.jenetics.incubator.property;

import static java.util.Collections.emptyIterator;

import java.util.Iterator;
import java.util.List;

public final class ListProperty extends CollectionProperty {

	ListProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject, path, value);
	}

	@Override
	public int size() {
		return value != null ? value().size() : 0;
	}

	public Object get(final int index) {
		if (value == null) {
			throw new IndexOutOfBoundsException("List is null.");
		}
		return value().get(index);
	}

	@Override
	public Iterator<Object> iterator() {
		return value != null ? value().iterator() : emptyIterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> value() {
		return (List<Object>)value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
