package io.jenetics.incubator.property;

import static java.util.Collections.emptyIterator;

import java.util.Collection;
import java.util.Iterator;

public sealed class CollectionProperty
	extends IterableProperty
	permits ListProperty, SetProperty
{

	CollectionProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject, path, value);
	}

	public int size() {
		return value != null ? value().size() : 0;
	}

	@Override
	public Iterator<Object> iterator() {
		return value != null ? value().iterator() : emptyIterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Object> value() {
		return (Collection<Object>)value;
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
