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
import static org.jenetics.internal.util.object.eq;

import java.io.Serializable;
import java.util.DoubleSummaryStatistics;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-05-07 $</em>
 */
public final class DoubleSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long _count;
	private final double _min;
	private final double _max;
	private final double _sum;
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
	public DoubleSummary(
		final long count,
		final double min,
		final double max,
		final double sum,
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
	 * Return the minimum value recorded, or {@code Integer.MAX_VALUE} if no
	 * values have been recorded.
	 *
	 * @return the minimum value, or {@code Integer.MAX_VALUE} if none
	 */
	public double getMin() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Integer.MIN_VALUE} if no
	 * values have been recorded.
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

	@Override
	public int hashCode() {
		return Hash.of(DoubleMoments.class)
			.and(_count)
			.and(_sum)
			.and(_min)
			.and(_max)
			.and(_mean).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == null) {
			return true;
		}
		if (!(object instanceof DoubleSummary)) {
			return false;
		}

		final DoubleSummary summary = (DoubleSummary)object;
		return eq(_count, summary._count) &&
			eq(_sum, summary._sum) &&
			eq(_min, summary._min) &&
			eq(_max, summary._max) &&
			eq(_mean, summary._mean);
	}

	@Override
	public String toString() {
		return String.format(
			"DoubleSummary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s]",
			getCount(), getMin(), getMax(), getSum(), getMean()
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

	public static <T> Collector<T, ?, DoubleSummary>
	collector(final ToDoubleFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			DoubleSummaryStatistics::new,
			(a, b) -> a.accept(mapper.applyAsDouble(b)),
			(a, b) -> {a.combine(b); return a;},
			DoubleSummary::of
		);
	}

}
