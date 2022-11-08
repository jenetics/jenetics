package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

public final class SimpleProperty implements Property {

	private final PropertyDescription desc;
	private final Object enclosingObject;
	private final Path path;
	private final Object value;

	SimpleProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		this.desc = requireNonNull(desc);
		this.enclosingObject = requireNonNull(enclosingObject);
		this.path = requireNonNull(path);
		this.value = value;
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
		return desc.type();
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Reader reader() {
		return this::read;
	}

	private Object read() {
		return desc.read(enclosingObject);
	}

	@Override
	public Optional<Writer> writer() {
		return desc.isWriteable()
			? Optional.of(this::write)
			: Optional.empty();
	}

	private boolean write(final Object value) {
		return desc.write(enclosingObject, value);
	}

	@Override
	public String toString() {
		return Properties.toString(SimpleProperty.class.getSimpleName(), this);
	}

}
