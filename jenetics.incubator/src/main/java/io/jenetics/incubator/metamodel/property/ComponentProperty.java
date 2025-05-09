package io.jenetics.incubator.metamodel.property;

import static java.util.Objects.requireNonNull;

import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.type.ComponentType;
import io.jenetics.incubator.metamodel.type.StructType;

/**
 * The component property of a struct property.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class ComponentProperty
	extends PropertyDelegates
	implements EnclosedProperty, ConcreteProperty
{
	private final StructType enclosureType;

	ComponentProperty(final PropParam param, final StructType enclosureType) {
		super(param);
		this.enclosureType = requireNonNull(enclosureType);
	}

	@Override
	public StructType enclosureType() {
		return enclosureType;
	}

	@Override
	public ComponentType type() {
		return (ComponentType)param.type();
	}

	@Override
	public Accessor accessor() {
		return type().accessor().of(read());
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
