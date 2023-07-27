package io.jenetics.incubator.beans.statical;

import java.util.function.ToIntFunction;

import static java.util.Objects.requireNonNull;

public record IndexedDescription(
	Class<?> type,
	ToIntFunction<Object> size,
	IndexedGetter getter,
	IndexedSetter setter
)
	implements Description
{
	public IndexedDescription {
		requireNonNull(type);
		requireNonNull(getter);
	}

	@Override
	public String name() {
		return "[*]";
	}

}
