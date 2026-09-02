package io.jenetics.incubator.util.function;

public sealed interface Result<T> {
	record OK<T>(T value) implements Result<T> {}
	record NOK<T>(Throwable error) implements Result<T> {}
}
