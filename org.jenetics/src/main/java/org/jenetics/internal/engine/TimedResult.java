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

import java.util.function.Function;
import java.util.function.Supplier;

import org.jenetics.internal.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-04 $</em>
 */
public class TimedResult<T> {
	private final Timer _timer;
	private final T _result;

	private TimedResult(final Timer timer, final T result) {
		_timer = timer;
		_result = result;
	}

	public static <T> Supplier<TimedResult<T>> of(final Supplier<T> result) {
		return () -> {
			final Timer timer = Timer.of();
			timer.start();
			try {
				return new TimedResult<>(timer, result.get());
			} finally {
				timer.stop();
			}
		};
	}

	public static <T, R> Function<T, TimedResult<R>> of(final Function<T, R> fn) {
		return v -> {
			final Timer timer = Timer.of();
			timer.start();
			try {
				return new TimedResult<>(timer, fn.apply(v));
			} finally {
				timer.stop();
			}
		};
	}

	public T get() {
		return _result;
	}

	public Timer getTimer() {
		return _timer;
	}

}
