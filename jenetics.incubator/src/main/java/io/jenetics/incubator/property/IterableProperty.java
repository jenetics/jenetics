package io.jenetics.incubator.property;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed class IterableProperty
	implements Iterable<Object>, Property
	permits ArrayProperty, ListProperty, MapProperty, SetProperty
{

	private final Object enclosingObject;
	private final Path path;
	private final Class<?> type;
	final Object value;

	private final List<Object> elements;

	IterableProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Object value,
		final List<Object> elements
	) {
		this.enclosingObject = enclosingObject;
		this.path = path;
		this.type = type;
		this.value = value;
		this.elements = elements;
	}

	IterableProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Collection<?> value
	) {
		this(enclosingObject, path, type, value, List.copyOf(value));
	}

	@Override
	public Object enclosingObject() {
		return enclosingObject;
	}

	@Override
	public Path path() {
		return path;
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public Collection<?> value() {
		return (Collection<?>)value;
	}

	public int size() {
		return elements.size();
	}

	public Object get(final int index) {
		return elements.get(index);
	}

	@Override
	public Iterator<Object> iterator() {
		return elements.iterator();
	}

	public Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}
