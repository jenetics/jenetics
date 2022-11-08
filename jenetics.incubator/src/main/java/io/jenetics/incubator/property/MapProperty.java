package io.jenetics.incubator.property;

import java.util.List;
import java.util.Map;

public final class MapProperty
	extends AbstractCollectionProperty<Map<?, ?>>
	implements CollectionProperty
{

	MapProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Map<?, ?> value
	) {
		super(enclosingObject, path, type, value, List.copyOf(value.entrySet()));
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
