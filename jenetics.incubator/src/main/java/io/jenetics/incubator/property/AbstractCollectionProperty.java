package io.jenetics.incubator.property;

import java.util.Iterator;
import java.util.List;

import io.jenetics.incubator.property.Property.Path;

abstract class AbstractCollectionProperty<T, E> {

	private final Object enclosingObject;
	private final Path path;
	private final Class<?> type;
	private final Class<E> elementType;
	private final T value;

	private final List<E> elements;

	AbstractCollectionProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Class<E> elementType,
		final T value,
		final List<E> elements
	) {
		this.enclosingObject = enclosingObject;
		this.path = path;
		this.type = type;
		this.elementType = elementType;
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

	public Class<E> elementType() {
		return elementType;
	}

	public T value() {
		return value;
	}

	public int size() {
		return elements.size();
	}

	public E get(final int index) {
		return elements.get(index);
	}

	public Iterator<E> iterator() {
		return elements.iterator();
	}

}
