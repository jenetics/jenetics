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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.LongSupplier;

import io.jenetics.util.NanoClock;

/**
 * Timer implementation for measuring execution durations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 5.0
 */
final class Timing {

	@FunctionalInterface
	interface Task<T, E extends Exception> {
		T execute() throws E;
	}

	private final LongSupplier _nanoClock;

	private long _start = Long.MIN_VALUE;
	private long _stop = Long.MIN_VALUE;
	private long _nanos = 0;

	private Timing(final LongSupplier nanoClock) {
		_nanoClock = requireNonNull(nanoClock);
	}

	<T, E extends Exception> T timing(final Timing.Task<T, E> task) throws E {
		start();
		try {
			return task.execute();
		} finally {
			stop();
		}
	}

	/**
	 * Start the timer.
	 *
	 * @return {@code this} timer, for method chaining
	 */
	synchronized Timing start() {
		_start = _nanoClock.getAsLong();
		return this;
	}

	/**
	 * Stop the timer.
	 *
	 * @return {@code this} timer, for method chaining
	 */
	synchronized Timing stop() {
		_stop = _nanoClock.getAsLong();
		_nanos += _stop - _start;
		_start = Long.MIN_VALUE;
		_stop = Long.MIN_VALUE;
		return this;
	}

	private boolean isStarted() {
		return _start != Long.MIN_VALUE;
	}

	private boolean isStopped() {
		return _stop != Long.MIN_VALUE;
	}

	/**
	 * Return the duration between two consecutive {@link #start()} and
	 * {@link #stop()} calls.
	 *
	 * @return the duration between two {@code start} and {@code stop} calls
	 */
	synchronized Duration duration() {
		return isStarted()
			? Duration.ofNanos(_nanos + _nanoClock.getAsLong() - _start)
			: Duration.ofNanos(_nanos);
	}

	/**
	 * Return an new timer object which uses the given clock for measuring the
	 * execution time.
	 *
	 * @param clock the clock used for measuring the execution time
	 * @return a new timer
	 */
	static Timing of(final Clock clock) {
		requireNonNull(clock);
		return clock instanceof NanoClock
			? new Timing(System::nanoTime)
			: new Timing(() -> nanos(clock));
	}

	private static long nanos(final Clock clock) {
		final Instant now = clock.instant();
		return now.getEpochSecond()*NanoClock.NANOS_PER_SECOND + now.getNano();
	}

	/**
	 * Return an new timer object with the default clock implementation.
	 *
	 * @return a new timer
	 */
	static Timing of() {
		return new Timing(System::nanoTime);
	}

}
