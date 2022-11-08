package io.jenetics.incubator.property;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed class IterableProperty
	extends PropertyMethods
	implements Iterable<Object>, Property
	permits ArrayProperty, CollectionProperty, MapProperty
{

	private final Path path;
	final Object value;

	IterableProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject);
		this.path = path;
		this.value = value;
	}

	@Override
	public Path path() {
		return path;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<Object> iterator() {
		return ((Iterable<Object>)value).iterator();
	}

	@Override
	public Object value() {
		return null;
	}

	public Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
