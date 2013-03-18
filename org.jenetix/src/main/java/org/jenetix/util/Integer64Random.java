/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetix.util;

import static java.lang.Math.round;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.object;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz
 *         Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-03-18 $</em>
 */
public class Integer64Random implements NumberRandom<Integer64> {

	private final Random _random;

	public Integer64Random(final Random random) {
		_random = object.nonNull(random, "Random");
	}

	public Integer64Random() {
		this(RandomRegistry.getRandom());
	}

	@Override
	public Integer64 next(final Integer64 min, final Integer64 max) {
		return next(_random, min, max);
	}

	public static Integer64 next(final Random random, final Integer64 min,
			final Integer64 max) {

		return Integer64.valueOf(0);
	}

	public long nextLong(final Random random, final long min, final long max) {
		if (min >= max) {
			throw new IllegalArgumentException();
		}

		final long diff = (max - min) + 1;
		if (diff <= 0) {
			// the range is too wide to fit in a positive long (larger than
			// 2^63); as it covers
			// more than half the long range, we use directly a simple rejection
			// method
			while (true) {
				final long r = random.nextLong();
				if (r >= min && r <= max) {
					return r;
				}
			}
		} else if (diff < Integer.MAX_VALUE) {
			// we can shift the range and generate directly a positive int
			return min + random.nextInt((int) diff);
		} else {
			// we can shift the range and generate directly a positive long
			return min + nextLong(random, diff);
		}
	}

	private static long nextLong(final Random random, final long n) {
		if (n > 0) {
			//final byte[] byteArray = new byte[8];
			long bits;
			long val;
			do {
				/*
				random.nextBytes(byteArray);
				bits = 0;
				for (final byte b : byteArray) {
					bits = (bits << 8) | (((long) b) & 0xffL);
				}
				*/
				bits = random.nextLong() & 0x7fffffffffffffffL;
				val = bits % n;
			} while (bits - val + (n - 1) < 0);
			return val;
		}
		throw new IllegalArgumentException();
	}

	public static long nextLong2(
		final Random random,
		final long min,
		final long max
	) {
		return round(random.nextDouble()*(max - min)) + min;
	}




	public static void main(final String[] args) {
		final Random random = new LCG64ShiftRandom();

		long max = 11111;

		long start = System.nanoTime();
		for (int i = 0; i < 100000000; ++i) {
			nextLong(random, max);
		}
		long stop = System.nanoTime();
		System.out.println("nextLong: " + (stop - start)/1000_000_000.0);

		start = System.nanoTime();
		for (int i = 0; i < 100000000; ++i) {
			nextLong2(random, 0, max);
		}
		stop = System.nanoTime();
		System.out.println("nextLong2: " + (stop - start)/1000_000_000.0);
	}






















}
