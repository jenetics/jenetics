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
package org.jenetics.stat;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * <i>Value</i> objects which contains statistical moments.
 *
 * @see org.jenetics.stat.DoubleMomentStatistics
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class DoubleMoments implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long _count;
	private final double _min;
	private final double _max;
	private final double _sum;
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
	private DoubleMoments(
		final long count,
		final double min,
		final double max,
		final double sum,
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
	public long getCount() {
		return _count;
	}

	/**
	 * Return the minimum value recorded, or {@code Double.POSITIVE_INFINITY} if
	 * no values have been recorded.
	 *
	 * @return the minimum value, or {@code Integer.MAX_VALUE} if none
	 */
	public double getMin() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Double.NEGATIVE_INFINITY} if
	 * no values have been recorded.
	 *
	 * @return the maximum value, or {@code Integer.MIN_VALUE} if none
	 */
	public double getMax() {
		return _max;
	}

	/**
	 * Return the sum of values recorded, or zero if no values have been
	 * recorded.
	 *
	 * @return the sum of values, or zero if none
	 */
	public double getSum() {
		return _sum;
	}

	/**
	 * Return the arithmetic mean of values recorded, or zero if no values have
	 * been recorded.
	 *
	 * @return the arithmetic mean of values, or zero if none
	 */
	public double getMean() {
		return _mean;
	}

	/**
	 * Return the variance of values recorded, or {@code Double.NaN} if no
	 * values have been recorded.
	 *
	 * @return the variance of values, or {@code NaN} if none
	 */
	public double getVariance() {
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
	public double getSkewness() {
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
	public double getKurtosis() {
		return _kurtosis;
	}

	@Override
	public int hashCode() {
		return Hash.of(DoubleMoments.class)
			.and(_count)
			.and(_sum)
			.and(_min)
			.and(_max)
			.and(_mean)
			.and(_variance)
			.and(_skewness)
			.and(_kurtosis).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof DoubleMoments &&
			eq(_count, ((DoubleMoments)obj)._count) &&
			eq(_sum, ((DoubleMoments)obj)._sum) &&
			eq(_min, ((DoubleMoments)obj)._min) &&
			eq(_max, ((DoubleMoments)obj)._max) &&
			eq(_mean, ((DoubleMoments)obj)._mean) &&
			eq(_variance, ((DoubleMoments)obj)._variance) &&
			eq(_skewness, ((DoubleMoments)obj)._skewness) &&
			eq(_kurtosis, ((DoubleMoments)obj)._kurtosis);
	}

	@Override
	public String toString() {
		return String.format(
			"IntMoments[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			getCount(), getMin(), getMax(), getSum(),
			getMean(), getVariance(), getSkewness(), getKurtosis()
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
	public static DoubleMoments of(
		final long count,
		final double min,
		final double max,
		final double sum,
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis
	) {
		return new DoubleMoments(
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
	public static DoubleMoments of(final DoubleMomentStatistics statistics) {
		return new DoubleMoments(
			statistics.getCount(),
			statistics.getMin(),
			statistics.getMax(),
			statistics.getSum(),
			statistics.getMean(),
			statistics.getVariance(),
			statistics.getSkewness(),
			statistics.getKurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies an double-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final DoubleMoments moments = stream
	 *     .collect(toDoubleMoments(v -> v.doubleValue()));
	 * }</pre>
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, DoubleMoments>
	toDoubleMoments(final ToDoubleFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			DoubleMomentStatistics::new,
			(a, b) -> a.accept(mapper.applyAsDouble(b)),
			DoubleMomentStatistics::combine,
			DoubleMoments::of
		);
	}

}
