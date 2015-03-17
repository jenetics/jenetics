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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.jenetics.internal.util.Equality;

/**
 * Clock implementation with <i>nano</i> second precision.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class NanoClock extends Clock implements Serializable {

	private static final long serialVersionUID = -2456014222030125954L;

	private static final long EPOCH_NANOS = System.currentTimeMillis()*1_000_000;
	private static final long NANO_START = System.nanoTime();

	/**
	 * This constants holds the number of nano seconds of one second.
	 */
	public static final long NANOS_PER_SECOND = 1_000_000_000;

	private final ZoneId _zone;

	private NanoClock(final ZoneId zone)  {
		_zone = requireNonNull(zone, "zone");
	}

	@Override
	public ZoneId getZone() {
		return _zone;
	}

	@Override
	public Clock withZone(final ZoneId zone) {
		return zone.equals(_zone) ? this : new NanoClock(zone);
	}

	@Override
	public long millis() {
		return System.currentTimeMillis();
	}

	@Override
	public Instant instant() {
		final long now = System.nanoTime() - NANO_START + EPOCH_NANOS;
		return Instant.ofEpochSecond(now/NANOS_PER_SECOND, now%NANOS_PER_SECOND);
	}

	@Override
	public int hashCode() {
		return _zone.hashCode() + 1;
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.of(this, obj)
			.test(clock -> _zone.equals(clock._zone));
	}

	@Override
	public String toString() {
		return "NanoClock[" + _zone + "]";
	}

	/**
	 * This clock is based on the <i>nano</i> system clock. It uses
	 * {@link System#nanoTime()} resolution
	 * <p>
	 * Conversion from instant to date or time uses the specified time-zone.
	 * <p>
	 * The returned implementation is immutable, thread-safe and
	 * {@code Serializable}.
	 *
	 * @param zone  the time-zone to use to convert the instant to date-time
	 * @return a clock that uses the best available system clock in the
	 *         specified zone
	 * @throws java.lang.NullPointerException if the given {@code zone} is
	 *         {@code null}
	 */
	public static Clock system(final ZoneId zone) {
		return new NanoClock(zone);
	}

	/**
	 * This clock is based on the <i>nano</i> system clock. It uses
	 * {@link System#nanoTime()} resolution
	 * <p>
	 * Conversion from instant to date or time uses the specified time-zone.
	 * <p>
	 * The returned implementation is immutable, thread-safe and
	 * {@code Serializable}.
	 *
	 * @return a clock that uses the best available system clock in the
	 *         UTC zone
	 * @throws java.lang.NullPointerException if the given {@code zone} is
	 *         {@code null}
	 */
	public static Clock systemUTC() {
		return system(ZoneOffset.UTC);
	}

	/**
	 * This clock is based on the <i>nano</i> system clock. It uses
	 * {@link System#nanoTime()} resolution
	 * <p>
	 * Conversion from instant to date or time uses the specified time-zone.
	 * <p>
	 * The returned implementation is immutable, thread-safe and
	 * {@code Serializable}.
	 *
	 * @return a clock that uses the best available system clock in the
	 *         default zone
	 * @throws java.lang.NullPointerException if the given {@code zone} is
	 *         {@code null}
	 */
	public static Clock systemDefaultZone() {
		return system(ZoneId.systemDefault());
	}

}
