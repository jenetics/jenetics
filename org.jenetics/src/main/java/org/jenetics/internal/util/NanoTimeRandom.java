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

import org.jenetics.util.Random64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz
 *         Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-10 $</em>
 * @since @__version__@
 */
public class NanoTimeRandom extends Random64 {

	@Override
	public long nextLong() {
		//return mix(nano(), nano());
		//return System.nanoTime();
		//return mix(System.nanoTime(), System.currentTimeMillis());

		//final long a = mix(time(), time());
		//final long b = mix(time(), time());
		return mix(nano(), nano());
	}

	private static long time() {
		//return (System.nanoTime() << 32) | System.nanoTime();
		return toLong(
			System.nanoTime(),
			System.nanoTime(),
			System.nanoTime(),
			System.nanoTime(),
			System.nanoTime(),
			System.nanoTime(),
			System.nanoTime(),
			System.nanoTime()
		);
	}

	public static void main(final String[] args) {
		for (int i = 0; i < 10; ++i) {
			System.out.println(nano());
			//System.out.println(System.nanoTime());
		}
	}

	static long toLong(
		final long p1,
		final long p2,
		final long p3,
		final long p4,
		final long p5,
		final long p6,
		final long p7,
		final long p8
	) {
		return
			((p1 << 56) +
				((p2 & 255) << 48) +
				((p3 & 255) << 40) +
				((p4 & 255) << 32) +
				((p5 & 255) << 24) +
				((p6 & 255) << 16) +
				((p7 & 255) <<  8) +
				((p8 & 255)));
	}

	static long nano() {
		return
			((System.nanoTime() << 56) +
				((System.nanoTime() & 255) << 48) +
				((System.nanoTime() & 255) << 40) +
				((System.nanoTime() & 255) << 32) +
				((System.nanoTime() & 255) << 24) +
				((System.nanoTime() & 255) << 16) +
				((System.nanoTime() & 255) <<  8) +
				((System.nanoTime() & 255)));
	}

	private static int mix(final int a, final int b) {
		int c = a^b;
		c ^= c << 13;
		c ^= c >>> 17;
		c ^= c << 15;
		return c;
	}

	private static long mix(final long a, final long b) {


		long c = a^b;
		c ^= c << 17;
		c ^= c >>> 31;
		c ^= c << 8;
		return c;	}

	private static long toLong(final int a, final int b) {
		return ((long)a << 32) | b;
	}

	private static long objectHashSeed() {
		return ((long)(new Object().hashCode()) << 32) |
			new Object().hashCode();
	}

	// 13,17,15
}
