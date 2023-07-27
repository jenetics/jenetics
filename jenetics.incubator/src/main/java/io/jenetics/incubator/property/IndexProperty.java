package io.jenetics.incubator.property;

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
