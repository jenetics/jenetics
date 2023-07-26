package io.jenetics.incubator.property;

public record ElementProperty(
	Object enclosingObject,
	Property.Path path,
	Object value,
	Class<?> type
) implements Property {
	@Override
	public String toString() {
		return Properties.toString(ElementProperty.class.getSimpleName(), this);
	}
}
