package io.jenetics.incubator.metamodel.property;

public sealed interface ConcreteProperty
	extends Property
	permits
		ArrayProperty,
		BeanProperty,
		ComponentProperty,
		ElementProperty,
		IndexProperty,
		ListProperty,
		MapProperty,
		OptionalProperty,
		RecordProperty,
		SetProperty
{
}
