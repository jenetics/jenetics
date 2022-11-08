package io.jenetics.incubator.property;

import java.util.List;

public final class ListProperty
	extends AbstractCollectionProperty<List<?>>
	implements CollectionProperty
{

	@SuppressWarnings("unchecked")
	ListProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final List<?> value
	) {
		super(enclosingObject, path, type, value, (List<Object>)value);
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
