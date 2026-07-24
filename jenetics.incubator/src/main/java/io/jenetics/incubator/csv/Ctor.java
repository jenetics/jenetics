package io.jenetics.incubator.csv;

import java.util.function.Function;

@FunctionalInterface
public interface Ctor<T> {

	T apply(Row row);


	default Function<String[], T> map(StringFormat format) {
		return components -> null;
	}

}
