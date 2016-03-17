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
package org.jenetics.internal.math;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class arithmetic {
	private arithmetic() {require.noInstance();}


	/**
	 * Normalize the given double array, so that it sum to one. The
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
		final double sum = 1.0/statistics.sum(values);
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i]*sum;
		}

		return result;
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
}
