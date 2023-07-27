package io.jenetics.incubator.beans;

public record IndexProperty(
	Object enclosingObject,
	Property.Path path,
	Object value,
	Class<?> type
)
	implements Property
{
	@Override
	public String toString() {
		return Properties.toString(IndexProperty.class.getSimpleName(), this);
	}
}
