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
package org.jenetics.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class TimedResult<T> {
	private final Timer _timer;
	private final T _result;

	private TimedResult(final Timer timer, final T result) {
		_timer = requireNonNull(timer);
		_result = requireNonNull(result);
	}

	public static <T> Supplier<TimedResult<T>> of(
		final Supplier<? extends T> result
	) {
		return () -> {
			final Timer timer = Timer.of().start();
			try {
				return new TimedResult<>(timer, result.get());
			} finally {
				timer.stop();
			}
		};
	}

	public static <T, R> Function<T, TimedResult<R>> of(
		final Function<? super T, ? extends R> function
	) {
		return value -> {
			final Timer timer = Timer.of().start();
			try {
				return new TimedResult<>(timer, function.apply(value));
			} finally {
				timer.stop();
			}
		};
	}

	public static <T> TimedResult<T> of(final Timer timer, final T result) {
		return new TimedResult<>(timer, result);
	}

	public T get() {
		return _result;
	}

	public Timer getTimer() {
		return _timer;
	}

}
