package io.jenetics.incubator.property;

import java.util.Arrays;

public final class ArrayProperty
	extends AbstractCollectionProperty<Object[], Object>
	implements CollectionProperty<Object>
{

	ArrayProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Class<Object> elementType,
		final Object[] value
	) {
		super(
			enclosingObject,
			path,
			type,
			elementType,
			value,
			Arrays.asList(value)
		);
	}

}
