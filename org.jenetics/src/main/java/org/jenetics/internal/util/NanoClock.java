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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Clock implementation with <i>nano</i> second precision.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-07 $</em>
 */
public final class NanoClock extends Clock {

	public static final long NANOS_PER_SECOND = 1_000_000_000;

	private static final long EPOCH_NANOS = System.currentTimeMillis()*1_000_000;
	private static final long NANO_START = System.nanoTime();

	public static final NanoClock INSTANCE = new NanoClock();

	private final ZoneId _zone;

	/**
	 * Create an new clock instance with the given zone.
	 *
	 * @param zone the clock zone
	 * @throws java.lang.NullPointerException if the given {@code zone} is
	 *         {@code null}.
	 */
	public NanoClock(final ZoneId zone)  {
		_zone = requireNonNull(zone);
	}

	/**
	 * Create an new clock instance with UTC zone.
	 */
	public NanoClock() {
		this(ZoneOffset.UTC);
	}

	@Override
	public ZoneId getZone() {
		return _zone;
	}

	@Override
	public Clock withZone(final ZoneId zone) {
		return new NanoClock(zone);
	}

	@Override
	public Instant instant() {
		final long now = System.nanoTime() - NANO_START + EPOCH_NANOS;
		return Instant.ofEpochSecond(now/NANOS_PER_SECOND, now%NANOS_PER_SECOND);
	}

}
