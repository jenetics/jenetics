package io.jenetics.incubator.metamodel.property;

/**
 * The components of a struct property.
 */
public final class ComponentProperty
	extends PropertyDelegates
	implements EnclosedProperty, ConcreteProperty
{

	ComponentProperty(final PropParam param) {
		super(param);
	}

}
