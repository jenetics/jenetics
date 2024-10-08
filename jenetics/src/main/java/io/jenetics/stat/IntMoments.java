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

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

/**
 * <i>Value</i> objects which contains statistical moments.
 *
 * @see io.jenetics.stat.IntMomentStatistics
 *
 * @param count the count of values recorded
 * @param min the minimum value recorded, or {@link Integer#MAX_VALUE} if no
 * 	      values have been recorded
 * @param max the maximum value recorded, or {@link Integer#MIN_VALUE} if no
 * 	      values have been recorded
 * @param sum the sum of values recorded, or zero if no values have been recorded
 * @param mean the arithmetic mean of values recorded, or zero if no values have
 * 	      been recorded
 * @param variance the variance of values recorded, or {@link Double#NaN} if no
 * 	      values have been recorded
 * @param skewness the <a href="https://en.wikipedia.org/wiki/Skewness">Skewness</a>
 *        of values recorded, or {@link Double#NaN} if less than two values have
 *        been recorded
 * @param kurtosis the <a href="https://en.wikipedia.org/wiki/Kurtosis">Kurtosis</a>
 *        of values recorded, or {@link Double#NaN} if less than four values
 *        have been recorded
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 7.0
 */
public record IntMoments(
	long count,
	int min,
	int max,
	long sum,
	double mean,
	double variance,
	double skewness,
	double kurtosis
)
	implements Serializable
{

	@Serial
	private static final long serialVersionUID = 2L;

	@Override
	public String toString() {
		return String.format(
			"IntMoments[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			count(), min(), max(), sum(),
			mean(), variance(), skewness(), kurtosis()
		);
	}

	/**
	 * Return a new value object of the statistical moments, currently
	 * represented by the {@code statistics} object.
	 *
	 * @param statistics the creating (mutable) statistics class
	 * @return the statistical moments
	 */
	public static IntMoments of(final IntMomentStatistics statistics) {
		return new IntMoments(
			statistics.count(),
			statistics.min(),
			statistics.max(),
			statistics.sum(),
			statistics.mean(),
			statistics.variance(),
			statistics.skewness(),
			statistics.kurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which returns moments-statistics for the
	 * resulting values.
	 * {@snippet lang="java":
	 * final Stream<Integer> stream = null; // @replace substring='null' replacement="..."
	 * final IntMoments moments = stream.collect(toIntMoments());
	 * }
	 *
	 * @since 4.1
	 *
	 * @param <N> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 */
	public static <N extends Number> Collector<N, ?, IntMoments>
	toIntMoments() {
		return toIntMoments(Number::intValue);
	}

	/**
	 * Return a {@code Collector} which applies an int-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 * {@snippet lang="java":
	 * final Stream<SomeObject> stream = null; // @replace substring='null' replacement="..."
	 * final IntMoments moments = stream
	 *     .collect(toIntMoments(v -> v.intValue()));
	 * }
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, IntMoments>
	toIntMoments(final ToIntFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			IntMomentStatistics::new,
			(a, b) -> a.accept(mapper.applyAsInt(b)),
			IntMomentStatistics::combine,
			IntMoments::of
		);
	}

}
