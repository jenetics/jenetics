/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.util;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@code Try} type represents a computation that may either result in an
 * exception, or return a successfully computed value.
 *
 * @param <T> the result type for successful computations
 * @param <E> the exception type for failed computations
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public sealed interface Try<T, E extends Throwable> {

	T get() throws E;

	@SuppressWarnings("unchecked")
	default <B> Try<B, E> map(final Function<? super T, ? extends B> mapper) {
		return switch (this) {
			case Success(var value) -> new Success<>(mapper.apply(value));
			case Failure<?, E> failure -> (Try<B, E>)failure;
		};
	}

	@SuppressWarnings("unchecked")
	default <B> Try<B, E>
	flatMap(final Function<? super T, ? extends Try<? extends B, ? extends E>> mapper) {
		return switch (this) {
			case Success(var value) -> (Try<B, E>)mapper.apply(value);
			case Failure<?, E> failure -> (Try<B, E>)failure;
		};
	}

	default <B> B fold(
		final Function<? super E, ? extends B> fa,
		final Function<? super T, ? extends B> fb
	) {
		return switch (this) {
			case Success(var value) -> fb.apply(value);
			case Failure(var error) -> fa.apply(error);
		};
	}

	default T orElse(final T defaultValue) {
		return switch (this) {
			case Success(var value) -> value;
			case Failure(var error) -> defaultValue;
		};
	}

	default T orElseGet(final Supplier<? extends T> supplier) {
		return switch (this) {
			case Success(var value) -> value;
			case Failure(var error) -> supplier.get();
		};
	}

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
