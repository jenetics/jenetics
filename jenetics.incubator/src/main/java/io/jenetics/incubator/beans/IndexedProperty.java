package io.jenetics.incubator.beans;

public record IndexedProperty(
	Object enclosingObject,
	Property.Path path,
	Object value,
	Class<?> type
)
	implements Property
{
	@Override
	public String toString() {
		return Properties.toString(IndexedProperty.class.getSimpleName(), this);
	}
}
