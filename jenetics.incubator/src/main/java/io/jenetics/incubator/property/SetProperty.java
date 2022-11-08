package io.jenetics.incubator.property;

import static java.util.Collections.emptyIterator;

import java.util.Iterator;
import java.util.Set;

public final class SetProperty extends CollectionProperty {

	SetProperty(
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

	@Override
	public Iterator<Object> iterator() {
		return value != null ? value().iterator() : emptyIterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<Object> value() {
		return (Set<Object>)value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
