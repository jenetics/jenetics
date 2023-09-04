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
package io.jenetics.stat;

import static java.lang.Double.NaN;
import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.DoubleSummaryStatistics;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import io.jenetics.internal.math.DoubleAdder;

/**
 * <i>Value</i> objects which contains statistical summary information.
 *
 * @see java.util.DoubleSummaryStatistics
 *
 * @param count the count of values recorded
 * @param min the minimum value recorded, or {@link Double#POSITIVE_INFINITY} if
 *        no values have been recorded
 * @param max the maximum value recorded, or {@link Double#NEGATIVE_INFINITY} if
 * 	      no values have been recorded
 * @param sum the sum of values recorded, or zero if no values have been recorded
 * @param mean the arithmetic mean of values recorded, or zero if no values have
 * 	      been recorded
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 7.0
 */
public record DoubleSummary(
	long count,
	double min,
	double max,
	double sum,
	double mean
)
	implements Serializable
{
	@Serial
	private static final long serialVersionUID = 2L;

	@Override
	public String toString() {
		return String.format(
			"DoubleSummary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s]",
			count(), min(), max(), sum(), mean()
		);
	}

	/**
	 * Return a new value object of the statistical summary, currently
	 * represented by the {@code statistics} object.
	 *
	 * @param statistics the creating (mutable) statistics class
	 * @return the statistical moments
	 */
	public static DoubleSummary of(final DoubleSummaryStatistics statistics) {
		return new DoubleSummary(
			statistics.getCount(),
			statistics.getMin(),
			statistics.getMax(),
			statistics.getSum(),
			statistics.getAverage()
		);
	}

	/**
	 * Return a {@code Collector} which applies a double-producing mapping
	 * function to each input element, and return summary-statistics for the
	 * resulting values.
	 *
	 * {@snippet lang="java":
	 * final Stream<SomeObject> stream = null; // @replace substring='null' replacement="..."
	 * final DoubleSummary summary = stream
	 *     .collect(toDoubleSummary(v -> v.doubleValue()));
	 * }
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the summary-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, DoubleSummary>
	toDoubleSummary(final ToDoubleFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			DoubleSummaryStatistics::new,
			(a, b) -> a.accept(mapper.applyAsDouble(b)),
			(a, b) -> {a.combine(b); return a;},
			DoubleSummary::of
		);
	}


	/* *************************************************************************
	 * Some static helper methods.
	 **************************************************************************/

	/**
	 * Return the minimum value of the given double array.
	 *
	 * @since 4.0
	 *
	 * @param values the double array.
	 * @return the minimum value or {@link Double#NaN} if the given array is
	 *         empty.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double min(final double[] values) {
		double min = NaN;
		if (values.length > 0) {
			min = values[0];

			for (double value : values) {
				if (value < min) {
					min = value;
				}
			}
		}

		return min;
	}

	/**
	 * Return the maximum value of the given double array.
	 *
	 * @since 4.0
	 *
	 * @param values the double array.
	 * @return the maximum value or {@link Double#NaN} if the given array is
	 *         empty.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double max(final double[] values) {
		double max = NaN;
		if (values.length > 0) {
			max = values[0];

			for (double value : values) {
				if (value > max) {
					max = value;
				}
			}
		}

		return max;
	}

	/**
	 * Return the sum of the given double array.
	 *
	 * @since 4.0
	 *
	 * @param values the values to sum up.
	 * @return the sum of the given {@code values}.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double sum(final double[] values) {
		return DoubleAdder.sum(values);
	}

	/**
	 * Returns a double describing the arithmetic mean of the values, or
	 * {@link Double#NaN} if the {@code values} array is empty.
	 *
	 * @since 4.0
	 *
	 * @param values the values to calculate the mean of
	 * @return the arithmetic mean of the given {@code values} or
	 *         {@link Double#NaN} if the {@code values} array is empty
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public static double mean(final double[] values) {
		return values.length > 0 ? sum(values)/values.length : NaN;
	}

}
