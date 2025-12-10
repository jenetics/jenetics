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

import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * design of the {@link java.util.IntSummaryStatistics} class.
 * <p>
 * This class is designed to work with (though does not require) streams. For
 * example, you can compute moments-statistics on a stream of ints with:
 * {@snippet lang="java":
 * final IntStream stream = null; // @replace substring='null' replacement="..."
 * final IntMomentStatistics statistics = stream.collect(
 *         IntMomentStatistics::new,
 *         IntMomentStatistics::accept,
 *         IntMomentStatistics::combine
 *     );
 * }
 *
 * For a non-int stream, you can use a collector:
 * {@snippet lang="java":
 * final Stream<SomeObject> stream = null; // @replace substring='null' replacement="..."
 * final IntMomentStatistics statistics = stream
 *     .collect(toIntMomentStatistics(v -> v.intValue()));
 * }
 *
 * @implNote
 * This implementation is not thread safe. However, it is safe to use
 * {@link #toIntMomentStatistics(ToIntFunction)}  on a parallel stream, because
 * the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.
 *
 * @see java.util.IntSummaryStatistics
 * @see io.jenetics.stat.IntMoments
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
 */
public class IntMomentStatistics
	extends MomentStatistics
	implements IntConsumer
{

	private int _min = Integer.MAX_VALUE;
	private int _max = Integer.MIN_VALUE;
	private long _sum = 0L;

	/**
	 * Create an empty moments object.
	 */
	public IntMomentStatistics() {
	}

	/**
	 * Records a new value into the moment information
	 *
	 * @param value the input {@code value}
	 */
	@Override
	public void accept(final int value) {
		super.accept(value);
		_min = Math.min(_min, value);
		_max = Math.max(_max, value);
		_sum += value;
	}

	/**
	 * Combine two {@code IntMoments} statistic objects.
	 *
	 * @param other the other {@code IntMoments} statistics to combine with
	 *        {@code this} one.
	 * @return {@code this} statistics object
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public IntMomentStatistics combine(final IntMomentStatistics other) {
		super.combine(other);
		_min = Math.min(_min, other._min);
		_max = Math.max(_max, other._max);
		_sum += other._sum;

		return this;
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
	 * Compares the state of two {@code IntMomentStatistics} objects. This is
	 * a replacement for the {@link #equals(Object)} which is not advisable to
	 * implement for this mutable object. If two objects have the same state, it
	 * has still the same state when updated with the same value.
	 * {@snippet lang="java":
	 * final IntMomentStatistics ims1 = null; // @replace substring='null' replacement="..."
	 * final IntMomentStatistics ims2 = null; // @replace substring='null' replacement="..."
	 *
	 * if (ims1.sameState(ims2)) {
	 *     final int value = random.nextInt(1_000_000);
	 *     ims1.accept(value);
	 *     ims2.accept(value);
	 *
	 *     assert ims1.sameState(ims2);
	 *     assert ims2.sameState(ims1);
	 *     assert ims1.sameState(ims1);
	 * }
	 * }
	 *
	 * @since 3.7
	 *
	 * @param other the other object for the test
	 * @return {@code true} the {@code this} and the {@code other} objects have
	 *         the same state, {@code false} otherwise
	 */
	public boolean sameState(final IntMomentStatistics other) {
		return _min == other._min &&
			_max == other._max &&
			_sum == other._sum &&
			super.sameState(other);
	}

	/**
	 * Return an {@code IntMoments} object from the current statistics,
	 *
	 * @since 3.9
	 *
	 * @return an {@code IntMoments} object from the current statistics
	 */
	public IntMoments toIntMoments() {
		return IntMoments.of(this);
	}

	@Override
	public String toString() {
		return String.format(
			"IntMomentStatistics[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			count(), min(), max(), sum(),
			mean(), variance(), skewness(), kurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies an int-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 * {@snippet lang="java":
	 * final Stream<SomeObject> stream = null; // @replace substring='null' replacement="..."
	 * final IntMomentStatistics statistics = stream
	 *     .collect(toIntMomentStatistics(v -> v.intValue()));
	 * }
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, IntMomentStatistics>
	toIntMomentStatistics(final ToIntFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			IntMomentStatistics::new,
			(r, t) -> r.accept(mapper.applyAsInt(t)),
			IntMomentStatistics::combine
		);
	}

}
