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
package io.jenetics.internal.math;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.math.Probabilities.isOne;
import static io.jenetics.internal.math.Probabilities.isZero;
import static io.jenetics.internal.util.Requires.probability;

import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

/**
 * Some random helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 7.0
 */
public final class Randoms {
	private Randoms() {
	}

	public static byte nextByte(final RandomGenerator random) {
		return (byte)random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
	}

	public static char nextChar(final RandomGenerator random) {
		final class Limits {
			static final int LEFT = '0';
			static final int RIGHT = 'z' + 1;
		}

		char c = '\0';
		do {
			c = (char)random.nextInt(Limits.LEFT, Limits.RIGHT);
		} while (!((c <= 57 || c >= 65) && (c <= 90 || c >= 97)));

		return c;
	}

	public static short nextShort(final RandomGenerator random) {
		return (short)random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1);
	}

	public static String nextASCIIString(
		final int length,
		final RandomGenerator random
	) {
		final char[] chars = new char[length];
		for (int i = 0; i < length; ++i) {
			chars[i] = (char)random.nextInt(32, 127);
		}

		return new String(chars);
	}

	public static String nextASCIIString(final RandomGenerator random) {
		return nextASCIIString(random.nextInt(5, 20), random);
	}

	/**
	 * Create an {@code IntStream} which creates random indexes within the
	 * given range and the index probability.
	 *
	 * @since 3.0
	 *
	 * @param random the random engine used for calculating the random
	 *        indexes
	 * @param start the start index (inclusively)
	 * @param end the end index (exclusively)
	 * @param p the index selection probability
	 * @return a new random index stream
	 * @throws IllegalArgumentException if {@code p} is not a
	 *         valid probability.
	 */
	public static IntStream indexes(
		final RandomGenerator random,
		final int start,
		final int end,
		final double p
	) {
		requireNonNull(random);
		probability(p);

		if (isZero(p)) {
			return IntStream.empty();
		} else if (isOne(p)) {
			return IntStream.range(start, end);
		} else {
			final int P = Probabilities.toInt(p);
			return IntStream.range(start, end)
				.filter(_ -> random.nextInt() < P);
		}
	}


	/**
	 * Create an {@code IntStream} which creates random indexes within the
	 * given range and the index probability.
	 *
	 * @since 3.0
	 *
	 * @param random the random engine used for calculating the random
	 *        indexes
	 * @param n the end index (exclusively). The start index is zero.
	 * @param p the index selection probability
	 * @return a new random index stream
	 * @throws IllegalArgumentException if {@code p} is not a
	 *         valid probability.
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	public static IntStream indexes(
		final RandomGenerator random,
		final int n,
		final double p
	) {
		return indexes(random, 0, n, p);
	}

	/**
	 * Create a new <em>seed</em> byte array of the given length.
	 *
	 * @see #seed(byte[])
	 * @see #seed()
	 *
	 * @param length the length of the returned byte array.
	 * @return a new <em>seed</em> byte array of the given length
	 * @throws NegativeArraySizeException if the given length is smaller
	 *         than zero.
	 */
	public static byte[] seedBytes(final int length) {
		return seed(new byte[length]);
	}

	/**
	 * Fills the given byte array with random bytes, created by successive
	 * calls of the {@link #seed()} method.
	 *
	 * @see #seed()
	 *
	 * @param seed the byte array seed to fill with random bytes.
	 * @return the given byte array, for method chaining.
	 * @throws NullPointerException if the {@code seed} array is
	 *         {@code null}.
	 */
	public static byte[] seed(final byte[] seed) {
		for (int i = 0, len = seed.length; i < len;) {
			int n = Math.min(len - i, Long.SIZE/Byte.SIZE);

			for (long x = seed(); n-- > 0; x >>= Byte.SIZE) {
				seed[i++] = (byte)x;
			}
		}

		return seed;
	}

	/**
	 * Calculating a 64-bit seed value which can be used for initializing
	 * PRNGs. This method uses a combination of {@code System.nanoTime()}
	 * and {@code new Object().hashCode()} calls to create a reasonable safe
	 * seed value:
	 * {@snippet lang="java":
	 * public static long seed() {
	 *     return seed(System.nanoTime());
	 * }
	 * }
	 *
	 * This method passes all the statistical tests of the
	 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">
	 * dieharder</a> test suite&mdash;executed on a linux machine with
	 * JDK version 1.7. <em>Since there is no prove that this will the case
	 * for every Java version and OS, it is recommended to only use this
	 * method for seeding other PRNGs.</em>
	 *
	 * @see #seed(long)
	 *
	 * @return the random seed value.
	 */
	public static long seed() {
		return seed(System.nanoTime());
	}

	/**
	 * Uses the given {@code base} value to create a reasonable safe seed
	 * value. This is done by combining it with values of
	 * {@code new Object().hashCode()}:
	 * {@snippet lang="java":
	 * public static long seed(final long base) {
	 *     final long objectHashSeed = ((long)(new Object().hashCode()) << 32) |
	 *                                         new Object().hashCode();
	 *     long seed = base^objectHashSeed;
	 *     seed ^= seed << 17;
	 *     seed ^= seed >>> 31;
	 *     seed ^= seed << 8;
	 *     return seed;
	 * }
	 * }
	 *
	 * @param base the base value of the seed to create
	 * @return the created seed value.
	 */
	public static long seed(final long base) {
		return mix(base, ObjectSeed.seed());
	}

	private static long mix(final long a, final long b) {
		long c = a^b;
		c ^= c << 17;
		c ^= c >>> 31;
		c ^= c << 8;
		return c;
	}

	private static final class ObjectSeed {
		private static long seed() {
			return (long)new ObjectSeed().hashCode() << 32 |
				new ObjectSeed().hashCode();
		}
	}

}
