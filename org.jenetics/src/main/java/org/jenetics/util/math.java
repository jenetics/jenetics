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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

/**
 * Object with mathematical functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.1 &mdash; <em>$Date: 2013-01-30 $</em>
 */
public final class math extends StaticObject {
	private math() {}

	/**
	 * Add to long values and throws an ArithmeticException in the case of an
	 * overflow.
	 *
	 * @param a the first summand.
	 * @param b the second summand.
	 * @return the sum of the given values.
	 * @throws ArithmeticException if the summation would lead to an overflow.
	 */
	public static long plus(final long a, final long b) {
		if (a == Long.MIN_VALUE && b == Long.MIN_VALUE) {
			throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
		}

		final long z = a + b;
		if (a > 0) {
			if (b > 0 && z < 0) {
				throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
			}
		} else if (b < 0 && z > 0) {
			throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
		}

		return z;
	}

	/**
	 * Subtracts to long values and throws an ArithmeticException in the case of an
	 * overflow.
	 *
	 * @param a the minuend.
	 * @param b the subtrahend.
	 * @return the difference of the given values.
	 * @throws ArithmeticException if the subtraction would lead to an overflow.
	 */
	public static long minus(final long a, final long b) {
		final long z = a - b;
		if (a > 0) {
			if (b < 0 && z < 0) {
				throw new ArithmeticException(String.format("Overflow: %d - %d", a, b));
			}
		} else if (b > 0 && z > 0) {
			throw new ArithmeticException(String.format("Overflow: %d - %d", a, b));
		}

		return z;
	}

	/**
	 * Implementation of the <a href="http://en.wikipedia.org/wiki/Kahan_summation_algorithm">
	 * Kahan summation algorithm</a>.
	 *
	 * @param values the values to sum up.
	 * @return the sum of the given {@code values}.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double sum(final double[] values) {
		double sum = 0.0;
		double c = 0.0;
		double y = 0.0;
		double t = 0.0;

		for (int i = values.length; --i >= 0;) {
			y = values[i] - c;
			t = sum + y;
			c = t - sum - y;
			sum = t;
		}

		return sum;
	}

	/**
	 * Add the values of the given array.
	 *
	 * @param values the values to add.
	 * @return the values sum.
	 * @throws NullPointerException if the values are null;
	 */
	public static long sum(final long[] values) {
		long sum = 0;
		for (int i = values.length; --i >= 0;) {
			sum += values[i];
		}
		return sum;
	}

	/**
	 * Normalize the given double array, so that it sum to one. The normalization
	 * is performed in place and the same {@code values} are returned.
	 *
	 * @param values the values to normalize.
	 * @return the {@code values} array.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static double[] normalize(final double[] values) {
		final double sum = 1.0/sum(values);
		for (int i = values.length; --i >= 0;) {
			values[i] = values[i]*sum;
		}

		return values;
	}

	/**
	 * Return the minimum value of the given double array.
	 *
	 * @param values the double array.
	 * @return the minimum value or {@link Double#NaN} if the given array is empty.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double min(final double[] values) {
		double min = Double.NaN;
		if (values.length > 0) {
			min = values[0];

			for (int i = values.length; --i >= 1;) {
				if (values[i] < min) {
					min = values[i];
				}
			}
		}

		return min;
	}

	/**
	 * Return the maximum value of the given double array.
	 *
	 * @param values the double array.
	 * @return the maximum value or {@link Double#NaN} if the given array is empty.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double max(final double[] values) {
		double max = Double.NaN;
		if (values.length > 0) {
			max = values[0];

			for (int i = values.length; --i >= 1;) {
				if (values[i] > max) {
					max = values[i];
				}
			}
		}

		return max;
	}

	/**
	 * <i>Clamping</i> a value between a pair of boundary values.
	 * <i>Note: using clamp with floating point numbers may give unexpected
	 * results if one of the values is {@code NaN}.</i>
	 *
	 * @param v the value to <i>clamp</i>
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return The clamped value:
	 *        <ul>
	 *            <li>{@code lo if v < lo}</li>
	 *            <li>{@code hi if hi < v}</li>
	 *            <li>{@code otherwise, v}</li>
	 *        </ul>
	 */
	public static double clamp(final double v, final double lo, final double hi) {
		return v < lo ? lo : (v > hi ? hi : v);
	}

	/**
	 * Component wise multiplication of the given double array.
	 *
	 * @param values the double values to multiply.
	 * @param multiplier the multiplier.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static void times(final double[] values, final double multiplier) {
		for (int i = values.length; --i >= 0;) {
			values[i] *= multiplier;
		}
	}

	/**
	 * Component wise division of the given double array.
	 *
	 * @param values the double values to divide.
	 * @param divisor the divisor.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static void divide(final double[] values, final double divisor) {
		for (int i = values.length; --i >= 0;) {
			values[i] /= divisor;
		}
	}

	/**
	 * Binary exponentiation algorithm.
	 *
	 * @param b the base number.
	 * @param e the exponent.
	 * @return {@code b^e}.
	 */
	public static long pow(final long b, final long e) {
		long base = b;
		long exp = e;
		long result = 1;

		while (exp != 0) {
			if ((exp & 1) != 0) {
				result *= base;
			}
			exp >>>= 1;
			base *= base;
		}

		return result;
	}

	static int gcd(final int a, final int b) {
		int x = a;
		int y = b;
		int mod = x%y;

		while (mod != 0) {
			x = y;
			y = mod;
			mod = x%y;
		}

		return y;
	}

	static boolean isMultiplicationSave(final int a, final int b) {
		final long m = (long)a*(long)b;
		return m >= Integer.MIN_VALUE && m <= Integer.MAX_VALUE;
	}

	/**
	 * Return the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * distance of the given two double values.
	 *
	 * @param a first double.
	 * @param b second double.
	 * @return the ULP distance.
	 * @throws ArithmeticException if the distance doesn't fit in a long value.
	 */
	public static long ulpDistance(final double a, final double b) {
		return minus(ulpPosition(a), ulpPosition(b));
	}

	/**
	 * Calculating the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * position of a double number.
	 *
	 * [code]
	 * double a = 0.0;
	 * for (int i = 0; i < 10; ++i) {
	 *     a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
	 * }
	 *
	 * for (int i = 0; i < 19; ++i) {
	 *     a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
	 *     System.out.println(
	 *          a + "\t" + ulpPosition(a) + "\t" + ulpDistance(0.0, a)
	 *     );
	 * }
	 * [/code]
	 *
	 * The code fragment above will create the following output:
	 * <pre>
	 *   4.4E-323    9  9
	 *   4.0E-323    8  8
	 *   3.5E-323    7  7
	 *   3.0E-323    6  6
	 *   2.5E-323    5  5
	 *   2.0E-323    4  4
	 *   1.5E-323    3  3
	 *   1.0E-323    2  2
	 *   4.9E-324    1  1
	 *   0.0         0  0
	 *  -4.9E-324   -1  1
	 *  -1.0E-323   -2  2
	 *  -1.5E-323   -3  3
	 *  -2.0E-323   -4  4
	 *  -2.5E-323   -5  5
	 *  -3.0E-323   -6  6
	 *  -3.5E-323   -7  7
	 *  -4.0E-323   -8  8
	 *  -4.4E-323   -9  9
	 * </pre>
	 *
	 * @param a the double number.
	 * @return the ULP position.
	 */
	public static long ulpPosition(final double a) {
		long t = Double.doubleToLongBits(a);
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return t;
	}


	/**
	 * Mathematical functions regarding probabilities.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2013-01-30 $</em>
	 */
	static final class probability extends StaticObject {
		private probability() {}

		static final long INT_RANGE = pow(2, 32) - 1;


		/**
		 * Maps the probability, given in the range {@code [0, 1]}, to an
		 * integer in the range {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.
		 *
		 * @param probability the probability to widen.
		 * @return the widened probability.
		 */
		static int toInt(final double probability) {
			return (int)(Math.round(INT_RANGE*probability) + Integer.MIN_VALUE);
		}

	}

	/**
	 * Some helper method concerning random numbers and random seed generation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2013-01-30 $</em>
	 */
	public static final class random extends StaticObject {
		private random() {}

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
		 * @throws NullPointerException if the {@code seed} array is {@code null}.
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
		 * <p/>
		 * [code]
		 * public static long seed() {
		 *     return seed(System.nanoTime());
		 * }
		 * [/code]
		 * <p/>
		 * This method passes all of the statistical tests of the
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
		 * <p/>
		 * [code]
		 * public static long seed(final long base) {
		 *     final long objectHashSeed = ((long)(new Object().hashCode()) << 32) |
		 *                                         new Object().hashCode();
		 *     long seed = base ^ objectHashSeed;
		 *     seed ^= seed << 17;
		 *     seed ^= seed >>> 31;
		 *     seed ^= seed << 8;
		 *     return seed;
		 * }
		 * [/code]
		 *
		 * @param base the base value of the seed to create
		 * @return the created seed value.
		 */
		public static long seed(final long base) {
			long seed = base ^ objectHashSeed();
			seed ^= seed << 17;
			seed ^= seed >>> 31;
			seed ^= seed << 8;
			return seed;
		}


		private static long objectHashSeed() {
			return ((long)(new Object().hashCode()) << 32) | new Object().hashCode();
		}

	}


}





