package io.jenetics.incubator.property;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

record IndexedPropertyDescription(
	String name,
	Class<?> type,
	ToIntFunction<Object> size,
	IndexedGetter getter,
	IndexedSetter setter
)
	implements Description
{
	IndexedPropertyDescription {
		requireNonNull(name);
		requireNonNull(type);
		requireNonNull(getter);
	}
}
