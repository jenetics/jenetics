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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jenetics.internal.util.object.eq;

import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * {@link java.util.LongSummaryStatistics} class.
 * <p>
 * <b>Implementation note:</b>
 * <i>This implementation is not thread safe. However, it is safe to use
 * {@link #collector(java.util.function.ToLongFunction)}  on a parallel stream,
 * because the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.</i>
 *
 * @see java.util.LongSummaryStatistics
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-04-29 $</em>
 */
public class LongMoments extends Moments implements LongConsumer {

	private long _min = Long.MAX_VALUE;
	private long _max = Long.MIN_VALUE;
	private long _sum = 0L;

	/**
	 * Create an empty moments object.
	 */
	public LongMoments() {
	}

	/**
	 * Records a new value into the moments information
	 *
	 * @param value the input {@code value}
	 */
	@Override
	public void accept(final long value) {
		super.accept(value);
		_min = min(_min, value);
		_max = max(_max, value);
		_sum += value;
	}

	/**
	 * Combine two {@code LongMoments} statistic objects.
	 *
	 * @param other the other {@code LongMoments} statistics to combine with
	 *        {@code this} one.
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public void combine(final LongMoments other) {
		super.combine(other);
		_min = min(_min, other._min);
		_max = max(_max, other._max);
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

	@Override
	public int hashCode() {
		return Hash.of(LongMoments.class)
			.and(super.hashCode())
			.and(_min)
			.and(_max)
			.and(_sum).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == null) {
			return true;
		}
		if (!(object instanceof LongMoments)) {
			return false;
		}

		final LongMoments moments = (LongMoments)object;
		return super.equals(object) &&
			eq(_min, moments._min) &&
			eq(_max, moments._max) &&
			eq(_sum, moments._sum);
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s2=%s, S=%s, K=%s]",
			getCount(), _min, _max, _sum,
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies an long-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * [code]
	 * final LongMoments moments = objects.stream()
	 *     .collect(LongMoments.collector(v -&gt; v.longValue()));
	 * [/code]
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, LongMoments>
	collector(final ToLongFunction<? super T> mapper) {
		return Collector.of(
			LongMoments::new,
			(r, t) -> r.accept(mapper.applyAsLong(t)),
			(a, b) -> {a.combine(b); return a;}
		);
	}

}
