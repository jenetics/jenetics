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
package org.jenetix.internal;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static org.jenetics.internal.math.random.nextLong;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
public class random {
	private random() {require.noInstance();}


	/**
	 * Returns a pseudo-random, uniformly distributed int value between 0
	 * (inclusive) and the specified value (exclusive), drawn from the given
	 * random number generator's sequence.
	 *
	 * @param random the random engine used for creating the random number.
	 * @param n the bound on the random number to be returned. Must be
	 *        positive.
	 * @return the next pseudo-random, uniformly distributed int value
	 *         between 0 (inclusive) and n (exclusive) from the given random
	 *         number generator's sequence
	 * @throws IllegalArgumentException if n is smaller than 1.
	 * @throws NullPointerException if the given {@code random}
	 *         engine of the maximal value {@code n} is {@code null}.
	 */
	public static BigInteger nextBigInteger(
		final Random random,
		final BigInteger n
	) {
		if (n.compareTo(BigInteger.ONE) < 0) {
			throw new IllegalArgumentException(format(
				"n is smaller than one: %d", n
			));
		}

		BigInteger result = null;
		if (n.bitLength() <= Integer.SIZE - 1) {
			result = BigInteger.valueOf(random.nextInt(n.intValue()));
		} else if (n.bitLength() <= Long.SIZE - 1) {
			result = BigInteger.valueOf(nextLong(random, n.longValue()));
		} else {
			do {
				result = new BigInteger(n.bitLength(), random).mod(n);
			} while (result.compareTo(n) > 0);
		}

		return result;
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between min
	 * and max (min and max included).
	 *
	 * @param random the random engine to use for calculating the random
	 *        long value
	 * @param min lower bound for generated long integer (inclusively)
	 * @param max upper bound for generated long integer (inclusively)
	 * @return a random long integer greater than or equal to {@code min}
	 *         and less than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code min >= max}
	 * @throws NullPointerException if one of the given parameters
	 *         are {@code null}.
	 */
	public static BigInteger nextBigInteger(
		final Random random,
		final BigInteger min, final BigInteger max
	) {
		if (min.compareTo(max) >= 0) {
			throw new IllegalArgumentException(format(
				"min >= max: %d >= %d.", min, max
			));
		}

		final BigInteger n = max.subtract(min).add(BigInteger.ONE);
		return nextBigInteger(random, n).add(min);
	}

	public static BigInteger nextBigInteger(final Random random) {
		return new BigInteger(100, random);
	}

}
