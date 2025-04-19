package io.jenetics.incubator.metamodel.type;

public sealed interface ConcreteType
	permits
		ArrayType,
		BeanType,
		ElementType,
		IndexType,
		ListType,
		MapType,
		OptionalType,
		PropertyType,
		RecordType,
		SetType
{
}
