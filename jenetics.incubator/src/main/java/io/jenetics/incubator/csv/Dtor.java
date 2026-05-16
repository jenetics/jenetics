package io.jenetics.incubator.csv;

import java.util.function.Function;

@FunctionalInterface
public interface Dtor<T> {
	Row unapply(T value);

	default Function<T, String[]> map(StringFormat format) {
		return value -> null;
	}

}
