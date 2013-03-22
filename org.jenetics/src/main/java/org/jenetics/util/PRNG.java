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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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
package org.jenetics.util;

import java.util.Random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date: 2013-03-22 $</em>
 */
abstract class PRNG extends Random {

	private static final long serialVersionUID = 1L;

	protected PRNG(long seed) {
		super(seed);
	}

	protected PRNG() {
	}

	/**
	 * Returns a pseudorandom, uniformly distributed int value between min and
	 * max (end points included).
	 *
	 * @param min lower bound for generated integer
	 * @param max upper bound for generated integer
	 * @return a random integer greater than or equal to {@code min} and less
	 *         than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code min >= max}
	 */
	public int nextInt(final int min, final int max) {
		if (min >= max) {
			throw new IllegalArgumentException(String.format(
				"Min >= max: %d >= %d", min, max
			));
		}

		final int diff = max - min + 1;
		int result = 0;

		if (diff <= 0) {
			do {
				result = nextInt();
			} while (result < min || result > max);
		} else {
			result = nextInt(diff) + min;
		}

		return result;
	}

	/**
	 * Returns a pseudorandom, uniformly distributed int value between min
	 * and max (end points included).
	 *
	 * @param min lower bound for generated long integer
	 * @param max upper bound for generated long integer
	 * @return a random long integer greater than or equal to {@code min}
	 *         and less than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code min >= max}
	 */
	public long nextLong(final long min, final long max) {
		if (min >= max) {
			throw new IllegalArgumentException(String.format(
				"min >= max: %d >= %d.", min, max
			));
		}

		final long diff = (max - min) + 1;
		long result = 0;

		if (diff <= 0) {
			do {
				result = nextLong();
			} while (result < min || result > max);
		} else if (diff < Integer.MAX_VALUE) {
			result = nextInt((int)diff) + min;
		} else {
			result = nextLong(diff) + min;
		}

		return result;
	}


	/**
	 * Returns a pseudorandom, uniformly distributed int value between 0
	 * (inclusive) and the specified value (exclusive), drawn from the given
	 * random number generator's sequence.
	 *
	 * @param n the bound on the random number to be returned. Must be
	 *        positive.
	 * @return the next pseudorandom, uniformly distributed int value
	 *         between 0 (inclusive) and n (exclusive) from the given random
	 *         number generator's sequence
	 * @throws IllegalArgumentException if n is smaller than 1.
	 */
	public long nextLong(final long n) {
		if (n <= 0) {
			throw new IllegalArgumentException(String.format(
				"n is smaller than one: %d", n
			));
		}

		long bits;
		long result;
		do {
			bits = nextLong() & 0x7fffffffffffffffL;
			result = bits%n;
		} while (bits - result + (n - 1) < 0);

		return result;
	}

}
