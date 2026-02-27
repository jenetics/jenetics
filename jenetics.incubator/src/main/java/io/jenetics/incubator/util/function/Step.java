package io.jenetics.incubator.util.function;

@FunctionalInterface
public interface Step<T, R> {
	Result<R> apply(T value);
}
