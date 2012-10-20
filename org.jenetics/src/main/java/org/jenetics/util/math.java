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
 * @version 1.0
 */
public final class math {

	private math() {
		throw new AssertionError("Don't create an 'math' instance.");
	}

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
	public static long pow(final int b, final int e) {
		if (e < 0) {
			throw new IllegalArgumentException(String.format(
					"Exponent is negative: %d", e
				));
		}

		long base = b;
		int exp = e;
		long result = 1;

		while (exp != 0) {
			if ((exp & 1) != 0) {
				result *= base;
			}
			exp >>= 1;
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

}
