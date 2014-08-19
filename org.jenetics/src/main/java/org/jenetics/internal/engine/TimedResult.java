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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.engine;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Container class which contains the execution result and the execution time.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class TimedResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Duration _duration;
	private final T _result;

	private TimedResult(final Duration duration, final T result) {
		_duration = requireNonNull(duration);
		_result = requireNonNull(result);
	}

	/**
	 * Wraps the given supplier in a supplier which returns a {@code TimedResult}.
	 *
	 * @param supplier the supplier to wrap
	 * @param <T> the result type
	 * @return the wrapped supplier which returns a {@code TimedResult}
	 */
	public static <T> Supplier<TimedResult<T>> of(
		final Supplier<? extends T> supplier
	) {
		return () -> {
			final Timer timer = Timer.of().start();
			final T result = supplier.get();
			return new TimedResult<>(timer.stop().getTime(), result);
		};
	}

	/**
	 * Wraps the given function in a function which returns a
	 * {@code TimedResult}.
	 *
	 * @param function the function to wrap
	 * @param <T> the functions parameter type
	 * @param <R> the functions return type
	 * @return the wrapped function which returns a {@code TimedResult}
	 */
	public static <T, R> Function<T, TimedResult<R>> of(
		final Function<? super T, ? extends R> function
	) {
		return value -> {
			final Timer timer = Timer.of().start();
			final R result = function.apply(value);
			return new TimedResult<>(timer.stop().getTime(), result);
		};
	}

	/**
	 * Return the execution result.
	 *
	 * @return the execution result.
	 */
	public T get() {
		return _result;
	}

	/**
	 * Return the execution duration.
	 *
	 * @return the execution duration
	 */
	public Duration getDuration() {
		return _duration;
	}

}
