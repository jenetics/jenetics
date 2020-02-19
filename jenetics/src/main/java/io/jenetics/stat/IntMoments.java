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
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

/**
 * <i>Value</i> objects which contains statistical moments.
 *
 * @see io.jenetics.stat.IntMomentStatistics
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
 */
public final /*record*/ class IntMoments implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long _count;
	private final int _min;
	private final int _max;
	private final long _sum;
	private final double _mean;
	private final double _variance;
	private final double _skewness;
	private final double _kurtosis;


	/**
	 * Create an immutable object which contains statistical values.
	 *
	 * @param count the count of values recorded
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param sum the sum of the recorded values
	 * @param mean the arithmetic mean of values
	 * @param variance the variance of values
	 * @param skewness the skewness of values
	 * @param kurtosis the kurtosis of values
	 */
	private IntMoments(
		final long count,
		final int min,
		final int max,
		final long sum,
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis
	) {
		_count = count;
		_min = min;
		_max = max;
		_sum = sum;
		_mean = mean;
		_variance = variance;
		_skewness = skewness;
		_kurtosis = kurtosis;
	}

	/**
	 * Returns the count of values recorded.
	 *
	 * @return the count of recorded values
	 */
	public long count() {
		return _count;
	}

	/**
	 * Return the minimum value recorded, or {@code Integer.MAX_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the minimum value, or {@code Integer.MAX_VALUE} if none
	 */
	public int min() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Integer.MIN_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the maximum value, or {@code Integer.MIN_VALUE} if none
	 */
	public int max() {
		return _max;
	}

	/**
	 * Return the sum of values recorded, or zero if no values have been
	 * recorded.
	 *
	 * @return the sum of values, or zero if none
	 */
	public long sum() {
		return _sum;
	}

	/**
	 * Return the arithmetic mean of values recorded, or zero if no values have
	 * been recorded.
	 *
	 * @return the arithmetic mean of values, or zero if none
	 */
	public double mean() {
		return _mean;
	}

	/**
	 * Return the variance of values recorded, or {@code Double.NaN} if no
	 * values have been recorded.
	 *
	 * @return the variance of values, or {@code NaN} if none
	 */
	public double variance() {
		return _variance;
	}

	/**
	 * Return the skewness of values recorded, or {@code Double.NaN} if less
	 * than two values have been recorded.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Skewness">Skewness</a>
	 *
	 * @return the skewness of values, or {@code NaN} if less than two values
	 *         have been recorded
	 */
	public double skewness() {
		return _skewness;
	}

	/**
	 * Return the kurtosis of values recorded, or {@code Double.NaN} if less
	 * than four values have been recorded.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Kurtosis">Kurtosis</a>
	 *
	 * @return the kurtosis of values, or {@code NaN} if less than four values
	 *         have been recorded
	 */
	public double kurtosis() {
		return _kurtosis;
	}

	@Override
	public int hashCode() {
		return
			hash(_count,
			hash(_sum,
			hash(_min,
			hash(_max,
			hash(_mean,
			hash(_variance,
			hash(_skewness,
			hash(_kurtosis))))))));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof IntMoments &&
			_count == ((IntMoments)obj)._count &&
			_sum == ((IntMoments)obj)._sum &&
			_min == ((IntMoments)obj)._min &&
			_max == ((IntMoments)obj)._max &&
			Double.compare(_mean, ((IntMoments)obj)._mean) == 0 &&
			Double.compare(_variance, ((IntMoments)obj)._variance) == 0 &&
			Double.compare(_skewness, ((IntMoments)obj)._skewness) == 0 &&
			Double.compare(_kurtosis, ((IntMoments)obj)._kurtosis) == 0;
	}

	@Override
	public String toString() {
		return String.format(
			"IntMoments[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			count(), min(), max(), sum(),
			mean(), variance(), skewness(), kurtosis()
		);
	}

	/**
	 * Create an immutable object which contains statistical values.
	 *
	 * @param count the count of values recorded
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param sum the sum of the recorded values
	 * @param mean the arithmetic mean of values
	 * @param variance the variance of values
	 * @param skewness the skewness of values
	 * @param kurtosis the kurtosis of values
	 * @return an immutable object which contains statistical values
	 */
	public static IntMoments of(
		final long count,
		final int min,
		final int max,
		final long sum,
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis
	) {
		return new IntMoments(
			count,
			min,
			max,
			sum,
			mean,
			variance,
			skewness,
			kurtosis
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
	 *
	 * <pre>{@code
	 * final Stream<Integer> stream = ...
	 * final IntMoments moments = stream.collect(toIntMoments()));
	 * }</pre>
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
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final IntMoments moments = stream
	 *     .collect(toIntMoments(v -> v.intValue()));
	 * }</pre>
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
