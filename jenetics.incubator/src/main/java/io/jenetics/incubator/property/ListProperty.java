package io.jenetics.incubator.property;

import java.util.List;

public final class ListProperty
	extends AbstractCollectionProperty<List<?>, Object>
	implements CollectionProperty<Object>
{

	@SuppressWarnings("unchecked")
	ListProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Class<Object> elementType,
		final List<?> value
	) {
		super(
			enclosingObject,
			path,
			type,
			elementType,
			value,
			(List<Object>)value
		);
	}

}
