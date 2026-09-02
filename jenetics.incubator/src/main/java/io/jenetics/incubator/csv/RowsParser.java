package io.jenetics.incubator.csv;

import java.util.stream.Stream;

@FunctionalInterface
public interface RowsParser<T> {
	T parse(Stream<Row> rows);
}
