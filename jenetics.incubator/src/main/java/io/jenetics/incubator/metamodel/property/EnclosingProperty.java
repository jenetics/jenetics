package io.jenetics.incubator.metamodel.property;

public sealed interface EnclosingProperty
	extends Property
	permits CollectionProperty
{

	/**
	 * Return the size of the property.
	 *
	 * @return the size of the property
	 */
	int size();

}
