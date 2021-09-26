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

import static java.lang.Math.abs;
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
	private Randoms() {}

	public static byte nextByte(final RandomGenerator random) {
		return (byte)random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
	}

	public static char nextChar(final RandomGenerator random) {
		char c = '\0';
		do {
			c = (char)random.nextInt(
				Character.MIN_VALUE,
				Character.MAX_VALUE + 1
			);
		} while (!Character.isLetterOrDigit(c));

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

	/*
	 * Conversion methods used by the 'RandomGenerator' engine from the JDK.
	 */

	public static float toFloat(final int a) {
		return (a >>> 8)/(float)(1 << 24);
	}

	public static float toFloat(final long a) {
		return (int)(a >>> 40)/(float)(1 << 24);
	}

	public static double toDouble(final long a) {
		return (((a >>> 38) << 27) + ((int)a >>> 5))/(double)(1L << 53);
	}

	public static double toDouble(final int a, final int b) {
		return (((long)(a >>> 6) << 27) + (b >>> 5))/(double)(1L << 53);
	}


	/*
	 * Conversion methods used by the Apache Commons BitStreamGenerator.
	 */

	public static float toFloat2(final int a) {
		return (a >>> 9)*0x1.0p-23f;
	}

	public static float toFloat2(final long a) {
		return (int)(a >>> 41)*0x1.0p-23f;
	}

	public static double toDouble2(final long a) {
		return (a & 0xFFFFFFFFFFFFFL)*0x1.0p-52d;
	}

	public static double toDouble2(final int a, final int b) {
		return (((long)(a >>> 6) << 26) | (b >>> 6))*0x1.0p-52d;
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
	 * @return an new random index stream
	 * @throws IllegalArgumentException if {@code p} is not a
	 *         valid probability.
	 */
	public static IntStream indexes(
		final RandomGenerator random,
		final int start,
		final int end,
		final double p
	) {
		probability(p);
		final int P = Probabilities.toInt(p);

		return equals(p, 0, 1E-20)
			? IntStream.empty()
			: equals(p, 1, 1E-20)
				? IntStream.range(start, end)
				: IntStream.range(start, end)
					.filter(i -> random.nextInt() < P);
	}

	private static boolean
	equals(final double a, final double b, final double delta) {
		return abs(a - b) <= delta;
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
	 * @return an new random index stream
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
	 * Calculating a 64 bit seed value which can be used for initializing
	 * PRNGs. This method uses a combination of {@code System.nanoTime()}
	 * and {@code new Object().hashCode()} calls to create a reasonable safe
	 * seed value:
	 *
	 * <pre>{@code
	 * public static long seed() {
	 *     return seed(System.nanoTime());
	 * }
	 * }</pre>
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
	 *
	 * <pre>{@code
	 * public static long seed(final long base) {
	 *     final long objectHashSeed = ((long)(new Object().hashCode()) << 32) |
	 *                                         new Object().hashCode();
	 *     long seed = base^objectHashSeed;
	 *     seed ^= seed << 17;
	 *     seed ^= seed >>> 31;
	 *     seed ^= seed << 8;
	 *     return seed;
	 * }
	 * }</pre>
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
