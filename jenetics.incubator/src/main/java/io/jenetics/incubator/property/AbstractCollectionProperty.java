package io.jenetics.incubator.property;

import java.util.Iterator;
import java.util.List;

import io.jenetics.incubator.property.Property.Path;

abstract class AbstractCollectionProperty<C> {

	private final Object enclosingObject;
	private final Path path;
	private final Class<?> type;
	private final C value;

	private final List<Object> elements;

	AbstractCollectionProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final C value,
		final List<Object> elements
	) {
		this.enclosingObject = enclosingObject;
		this.path = path;
		this.type = type;
		this.value = value;
		this.elements = elements;
	}

	public Object enclosingObject() {
		return enclosingObject;
	}

	public Path path() {
		return path;
	}

	public Class<?> type() {
		return type;
	}

	public C value() {
		return value;
	}

	public int size() {
		return elements.size();
	}

	public Object get(final int index) {
		return elements.get(index);
	}

	public Iterator<Object> iterator() {
		return elements.iterator();
	}

}
