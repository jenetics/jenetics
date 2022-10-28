package io.jenetics.incubator.property;

import java.util.List;
import java.util.Set;

public final class SetProperty
	extends AbstractCollectionProperty<Set<?>, Object>
	implements CollectionProperty<Object>
{

	SetProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Class<Object> elementType,
		final Set<?> value
	) {
		super(
			enclosingObject,
			path,
			type,
			elementType,
			value,
			List.copyOf(value)
		);
	}

}
