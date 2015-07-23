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

import static java.lang.String.format;

import java.time.Clock;

import org.testng.annotations.Test;

import org.jenetics.test.Retry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class NanoClockTest extends Retry {

	@Test
	public void millis() { retry(3, () -> {
		final Clock nano = NanoClock.systemUTC();

		final long t2 = System.currentTimeMillis();
		final long t1 = nano.instant().toEpochMilli();

		assertEquals(t1, t2, 15);
	});}

	private static void assertEquals(final long v1, final long v2, final long epsilon) {
		final long diff = Math.abs(v1 - v2);
		if (diff > epsilon) {
			throw new AssertionError(format("Got %d but expected %d.", v1, v2));
		}
	}

}
