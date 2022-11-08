package io.jenetics.incubator.property;

import java.util.Arrays;

public final class ArrayProperty
	extends AbstractCollectionProperty<Object[]>
	implements CollectionProperty
{

	ArrayProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Object[] value
	) {
		super(enclosingObject, path, type, value, Arrays.asList(value));
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
