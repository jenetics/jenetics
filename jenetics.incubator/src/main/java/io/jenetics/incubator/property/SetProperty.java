package io.jenetics.incubator.property;

import java.util.List;
import java.util.Set;

public final class SetProperty
	extends AbstractCollectionProperty<Set<?>>
	implements CollectionProperty
{

	SetProperty(
		final Object enclosingObject,
		final Path path,
		final Class<?> type,
		final Set<?> value
	) {
		super(enclosingObject, path, type, value, List.copyOf(value));
	}

	@Override
	public String toString() {
		return Properties.toString(getClass().getSimpleName(), this);
	}

}
