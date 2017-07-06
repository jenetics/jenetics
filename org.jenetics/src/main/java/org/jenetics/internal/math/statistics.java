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
 * Some statistical (special) functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
public final class statistics {
	private statistics() {require.noInstance();}

	/**
	 * Return the minimum value of the given double array.
	 *
	 * @param values the double array.
	 * @return the minimum value or {@link Double#NaN} if the given array is
	 *         empty.
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
	 * @return the maximum value or {@link Double#NaN} if the given array is
	 *         empty.
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

}
