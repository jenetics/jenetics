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
import static java.util.Objects.requireNonNull;

import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * design of the {@link java.util.LongSummaryStatistics} class.
 * <p>
 * This class is designed to work with (though does not require) streams. For
 * example, you can compute moments-statistics on a stream of longs with:
 * <pre>{@code
 * final LongStream stream = ...
 * final LongMomentStatistics statistics = stream.collect(
 *         LongMomentStatistics::new,
 *         LongMomentStatistics::accept,
 *         LongMomentStatistics::combine
 *     );
 * }</pre>
 *
 * For a non long stream, you can use a collector:
 * <pre>{@code
 * final Stream<SomeObject> stream = ...
 * final LongMomentStatistics statistics = stream
 *     .collect(toLongMomentStatistics(v -> v.longValue()));
 * }</pre>
 *
 * <p>
 * <b>Implementation note:</b>
 * <i>This implementation is not thread safe. However, it is safe to use
 * {@link #toLongMomentStatistics(ToLongFunction)}  on a parallel stream, because the parallel
 * implementation of {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.</i>
 *
 * @see java.util.LongSummaryStatistics
 * @see org.jenetics.stat.LongMoments
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.7
 */
public class LongMomentStatistics
	extends MomentStatistics
	implements LongConsumer, IntConsumer
{

	private long _min = Long.MAX_VALUE;
	private long _max = Long.MIN_VALUE;
	private long _sum = 0L;

	/**
	 * Create an empty moments object.
	 */
	public LongMomentStatistics() {
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
	 * Records a new value into the moments information
	 *
	 * @param value the input {@code value}
	 */
	@Override
	public void accept(final int value) {
		accept((long)value);
	}

	/**
	 * Combine two {@code LongMoments} statistic objects.
	 *
	 * @param other the other {@code LongMoments} statistics to combine with
	 *        {@code this} one.
	 * @return {@code this} statistics object
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public LongMomentStatistics combine(final LongMomentStatistics other) {
		super.combine(other);
		_min = min(_min, other._min);
		_max = max(_max, other._max);
		_sum += other._sum;

		return this;
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
	 * Compares the state of two {@code LongMomentStatistics} objects. This is
	 * a replacement for the {@link #equals(Object)} which is not advisable to
	 * implement for this mutable object. If two object have the same state, it
	 * has still the same state when updated with the same value.
	 * <pre>{@code
	 * final LongMomentStatistics lms1 = ...;
	 * final LongMomentStatistics lms2 = ...;
	 *
	 * if (lms1.sameState(lms2)) {
	 *     final long value = random.nextInt(1_000_000);
	 *     lms1.accept(value);
	 *     lms2.accept(value);
	 *
	 *     assert lms1.sameState(lms2);
	 *     assert lms2.sameState(lms1);
	 *     assert lms1.sameState(lms1);
	 * }
	 * }</pre>
	 *
	 * @since 3.7
	 *
	 * @param other the other object for the test
	 * @return {@code true} the {@code this} and the {@code other} objects have
	 *         the same state, {@code false} otherwise
	 */
	public boolean sameState(final LongMomentStatistics other) {
		return _min == other._min &&
			_max == other._max &&
			_sum == other._sum &&
			super.sameState(other);
	}

	@Override
	public String toString() {
		return String.format(
			"LongMomentStatistics[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			getCount(), getMin(), getMax(), getSum(),
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies an long-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final LongMomentStatistics statistics = stream
	 *     .collect(toLongMomentStatistics(v -> v.longValue()));
	 * }</pre>
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, LongMomentStatistics>
	toLongMomentStatistics(final ToLongFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			LongMomentStatistics::new,
			(r, t) -> r.accept(mapper.applyAsLong(t)),
			LongMomentStatistics::combine
		);
	}

}
