package io.jenetics.incubator.property;

import java.util.Optional;

record SimplePropertyRecord(
	PropertyDescription desc,
	Object enclosingObject,
	Path path,
	Object value
)
	implements SimpleProperty
{

	@Override
	public Class<?> type() {
		return desc.type();
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
