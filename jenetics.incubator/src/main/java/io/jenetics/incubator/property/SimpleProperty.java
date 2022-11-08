package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

public final class SimpleProperty extends PropertyMethods implements Property {

	private final Path path;
	private final Object value;

	SimpleProperty(
		final PropertyDescription desc,
		final Object enclosingObject,
		final Path path,
		final Object value
	) {
		super(desc, enclosingObject);
		this.path = requireNonNull(path);
		this.value = value;
	}

	@Override
	public Path path() {
		return path;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public String toString() {
		return Properties.toString(SimpleProperty.class.getSimpleName(), this);
	}

}
