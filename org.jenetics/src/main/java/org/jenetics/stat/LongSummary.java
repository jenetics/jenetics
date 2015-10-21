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
import java.util.LongSummaryStatistics;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * <i>Value</i> objects which contains statistical summary information.
 *
 * @see java.util.LongSummaryStatistics
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class LongSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long _count;
	private final long _min;
	private final long _max;
	private final long _sum;
	private final double _mean;

	/**
	 * Create an immutable object which contains statistical summary values.
	 *
	 * @param count the count of values recorded
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param sum the sum of the recorded values
	 * @param mean the arithmetic mean of values
	 */
	private LongSummary(
		final long count,
		final long min,
		final long max,
		final long sum,
		final double mean
	) {
		_count = count;
		_min = min;
		_max = max;
		_sum = sum;
		_mean = mean;
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
	 * Return the minimum value recorded, or {@code Long.MAX_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the minimum value, or {@code Long.MAX_VALUE} if none
	 */
	public long getMin() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Long.MIN_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the maximum value, or {@code Long.MIN_VALUE} if none
	 */
	public long getMax() {
		return _max;
	}

	/**
	 * Return the sum of values recorded, or zero if no values have been
	 * recorded.
	 *
	 * @return the sum of values, or zero if none
	 */
	public long getSum() {
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

	@Override
	public int hashCode() {
		return Hash.of(LongMoments.class)
			.and(_count)
			.and(_sum)
			.and(_min)
			.and(_max)
			.and(_mean).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof LongSummary &&
			eq(_count, ((LongSummary)obj)._count) &&
			eq(_sum, ((LongSummary)obj)._sum) &&
			eq(_min, ((LongSummary)obj)._min) &&
			eq(_max, ((LongSummary)obj)._max) &&
			eq(_mean, ((LongSummary)obj)._mean);
	}

	@Override
	public String toString() {
		return String.format(
			"LongSummary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s]",
			getCount(), getMin(), getMax(), getSum(), getMean()
		);
	}

	/**
	 * Create an immutable object which contains statistical summary values.
	 *
	 * @param count the count of values recorded
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param sum the sum of the recorded values
	 * @param mean the arithmetic mean of values
	 * @return an immutable object which contains statistical values
	 */
	public static LongSummary of(
		final long count,
		final long min,
		final long max,
		final long sum,
		final double mean
	) {
		return new LongSummary(
			count,
			min,
			max,
			sum,
			mean
		);
	}

	/**
	 * Return a new value object of the statistical summary, currently
	 * represented by the {@code statistics} object.
	 *
	 * @param statistics the creating (mutable) statistics class
	 * @return the statistical moments
	 */
	public static LongSummary of(final LongSummaryStatistics statistics) {
		return new LongSummary(
			statistics.getCount(),
			statistics.getMin(),
			statistics.getMax(),
			statistics.getSum(),
			statistics.getAverage()
		);
	}

	/**
	 * Return a {@code Collector} which applies an long-producing mapping
	 * function to each input element, and returns summary-statistics for the
	 * resulting values.
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final LongSummary summary = stream
	 *     .collect(toLongSummary(v -> v.longValue()));
	 * }</pre>
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the summary-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, LongSummary>
	toLongSummary(final ToLongFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			LongSummaryStatistics::new,
			(a, b) -> a.accept(mapper.applyAsLong(b)),
			(a, b) -> {a.combine(b); return a;},
			LongSummary::of
		);
	}

}
