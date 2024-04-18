package io.jenetics.internal.util;

public sealed interface Try<T, E extends Throwable> {

	T get() throws E;

	record Success<T, E extends Throwable>(T value) implements Try<T, E> {
		@Override
		public T get() {
			return value;
		}
	}

	record Failure<T, E extends Throwable>(E error) implements Try<T, E> {
		@Override
		public T get() throws E {
			throw error;
		}
	}

}
