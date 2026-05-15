package io.jenetics.incubator.csv;

@FunctionalInterface
public interface StringParser<T> {
	T parse(String value);
}
