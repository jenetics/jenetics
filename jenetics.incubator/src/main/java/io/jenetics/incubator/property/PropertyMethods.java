package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import io.jenetics.incubator.property.Property.Reader;
import io.jenetics.incubator.property.Property.Writer;

abstract class PropertyMethods {

	final PropertyDescription desc;
	final Object enclosingObject;

	PropertyMethods(
		final PropertyDescription desc,
		final Object enclosingObject
	) {
		this.desc = requireNonNull(desc);
		this.enclosingObject = requireNonNull(enclosingObject);
	}

	public Object enclosingObject() {
		return enclosingObject;
	}

	public Class<?> type() {
		return desc.type();
	}

	public Reader reader() {
		return this::read;
	}

	private Object read() {
		return desc.read(enclosingObject);
	}

	public Optional<Writer> writer() {
		return desc.isWriteable()
			? Optional.of(this::write)
			: Optional.empty();
	}

	private boolean write(final Object value) {
		return desc.write(enclosingObject, value);
	}

}
