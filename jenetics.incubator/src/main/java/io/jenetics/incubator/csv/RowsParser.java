package io.jenetics.incubator.csv;

@FunctionalInterface
public interface RowsParser<T> {
	T parse(Rows rows);
}
