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
package io.jenetics.util;

import static java.lang.String.format;

import java.time.Clock;
import java.time.Instant;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class NanoClockTest {

	//@Test(invocationCount = 10, successPercentage = 70)
	public void millis() {
		final Clock nano = NanoClock.systemUTC();

		final long t2 = System.currentTimeMillis();
		final long t1 = nano.instant().toEpochMilli();

		assertEquals(t1, t2, 15);
	}

	private static void assertEquals(final long v1, final long v2, final long epsilon) {
		final long diff = Math.abs(v1 - v2);
		if (diff > epsilon) {
			throw new AssertionError(format("Got %d but expected %d.", v1, v2));
		}
	}

	//@Test
	public void perf() {
		final Instant[] instants = new Instant[500];
		final long[] nanos = new long[instants.length];

		final Clock clock = NanoClock.systemUTC();
		for (int i = 0; i < instants.length; ++i) {
			instants[i] = clock.instant();
		}

		for (int i = 0; i < instants.length; ++i) {
			nanos[i] = System.nanoTime();
		}

		for (int i = 1; i < instants.length; ++i) {
			final long diffSeconds = instants[i].getEpochSecond() - instants[i - 1].getEpochSecond();
			final int diffNanos = instants[i].getNano() - instants[i - 1].getNano();
			final long diff = diffSeconds*1_000_000_000 + diffNanos;

			System.out.println(diff + "\t->\t" + (nanos[i] - nanos[i - 1]));
		}
	}


}
