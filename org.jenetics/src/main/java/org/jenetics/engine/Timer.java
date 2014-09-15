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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.jenetics.internal.util.NanoClock;

/**
 * Timer implementation for measuring execution durations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-15 $</em>
 */
final class Timer {

	private final Clock _clock;

	private Instant _start;
	private Instant _stop;

	private Timer(final Clock clock) {
		_clock = requireNonNull(clock);
	}

	/**
	 * Start the timer.
	 *
	 * @return {@code this} timer, for method chaining
	 */
	public Timer start() {
		_start = _clock.instant();
		return this;
	}

	/**
	 * Stop the timer.
	 *
	 * @return {@code this} timer, for method chaining
	 */
	public Timer stop() {
		_stop = _clock.instant();
		return this;
	}

	/**
	 * Return the duration between two consecutive {@link #start()} and
	 * {@link #stop()} calls.
	 *
	 * @return the duration between two {@code start} and {@code stop} calls
	 */
	public Duration getTime() {
		return minus(_stop, _start);
	}

	private static Duration minus(final Instant a, final Instant b)  {
		final long seconds = a.getEpochSecond() - b.getEpochSecond();
		final long nanos = a.getNano() - b.getNano();

		return Duration.ofNanos(seconds*NanoClock.NANOS_PER_SECOND + nanos);
	}

	/**
	 * Return an new timer object which uses the given clock for measuring the
	 * execution time.
	 *
	 * @param clock the clock used for measuring the execution time
	 * @return a new timer
	 */
	public static Timer of(final Clock clock) {
		return new Timer(clock);
	}

	/**
	 * Return an new timer object with the default clock implementation.
	 *
	 * @return a new timer
	 */
	public static Timer of() {
		return of(NanoClock.INSTANCE);
	}

}
