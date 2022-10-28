package io.jenetics.incubator.property;

import java.util.List;
import java.util.Map;

public final class MapProperty
	extends AbstractCollectionProperty<Map<?, ?>, Map.Entry<?, ?>>
	implements CollectionProperty<Map.Entry<?, ?>>
{

	MapProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Class<Map.Entry<?, ?>> elementType,
		final Map<?, ?> value
	) {
		super(
			enclosingObject,
			path,
			type,
			elementType,
			value,
			List.copyOf(value.entrySet())
		);
	}

}
