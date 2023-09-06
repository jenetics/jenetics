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

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.min;

import io.jenetics.stat.LongSummary;

/**
 * This object contains mathematical helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.2
 */
public final class Basics {
	private Basics() {}

	/**
	 * Normalize the given double array, so that it sums to one. The
	 * normalization is performed in place and the same {@code values} are
	 * returned.
	 *
	 * @param values the values to normalize.
	 * @return the {@code values} array.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static double[] normalize(final double[] values) {
		final double sum = 1.0/DoubleAdder.sum(values);
		for (int i = values.length; --i >= 0;) {
			values[i] = values[i]*sum;
		}

		return values;
	}

	public static double[] normalize(final long[] values) {
		final double[] result = new double[values.length];
		final double sum = 1.0/LongSummary.sum(values);
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i]*sum;
		}

		return result;
	}

	public static double distance(double[] p1, double[] p2) {
		double sum = 0;
		for (int i = 0; i < p1.length; i++) {
			final double dp = p1[i] - p2[i];
			sum += dp * dp;
		}
		return Math.sqrt(sum);
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

	public static boolean isMultiplicationSave(final int a, final int b) {
		final long m = (long)a*(long)b;
		return (int)m == m;
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
		return v < lo ? lo : min(v, hi);
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
		return Math.subtractExact(ulpPosition(a), ulpPosition(b));
	}

	/**
	 * Calculating the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * position of a double number.
	 *
	 * {@snippet lang="java":
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
	 * }
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
		long t = doubleToLongBits(a);
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return t;
	}

}
