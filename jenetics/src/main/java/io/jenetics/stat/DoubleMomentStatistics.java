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

import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import io.jenetics.internal.math.DoubleAdder;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * {@link java.util.DoubleSummaryStatistics} class.
 * <p>
 * This class is designed to work with (though does not require) streams. For
 * example, you can compute moments-statistics on a stream of doubles with:
 * {@snippet lang="java":
 * final DoubleStream stream = null; // @replace substring='null' replacement="..."
 * final DoubleMomentStatistics statistics = stream.collect(
 *         DoubleMomentStatistics::new,
 *         DoubleMomentStatistics::accept,
 *         DoubleMomentStatistics::combine
 *     );
 * }
 *
 * For a non-double stream, you can use a collector:
 * {@snippet lang="java":
 * final Stream<SomeObject> stream = null; // @replace substring='null' replacement="..."
 * final DoubleMomentStatistics statistics = stream
 *     .collect(toDoubleMomentStatistics(v -> v.doubleValue()));
 * }
 *
 * @implNote
 * This implementation is not thread safe. However, it is safe to use
 * {@link #toDoubleMomentStatistics(ToDoubleFunction)}  on a parallel stream,
 * because the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.
 *
 * @see java.util.DoubleSummaryStatistics
 * @see io.jenetics.stat.DoubleMoments
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
 */
public class DoubleMomentStatistics
	extends MomentStatistics
	implements DoubleConsumer
{

	private double _min = Double.POSITIVE_INFINITY;
	private double _max = Double.NEGATIVE_INFINITY;

	private final DoubleAdder _sum = new DoubleAdder();

	/**
	 * Create an empty moments object.
	 */
	public DoubleMomentStatistics() {
	}

	/**
	 * Records a new value into the moment information
	 *
	 * @param value the input {@code value}
	 */
	@Override
	public void accept(final double value) {
		super.accept(value);
		_min = Math.min(_min, value);
		_max = Math.max(_max, value);
		_sum.add(value);
	}

	/**
	 * Combine two {@code DoubleMoments} statistic objects.
	 *
	 * @param other the other {@code DoubleMoments} statistics to combine with
	 *        {@code this} one.
	 * @return {@code this} statistics object
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public DoubleMomentStatistics combine(final DoubleMomentStatistics other) {
		super.combine(other);
		_min = Math.min(_min, other._min);
		_max = Math.max(_max, other._max);
		_sum.add(other._sum);

		return this;
	}

	/**
	 * Return the minimum value recorded, or {@code Double.POSITIVE_INFINITY} if
	 * no values have been recorded.
	 *
	 * @return the minimum value, or {@code Double.POSITIVE_INFINITY} if none
	 */
	public double min() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Double.NEGATIVE_INFINITY} if
	 * no values have been recorded.
	 *
	 * @return the maximum value, or {@code Double.NEGATIVE_INFINITY} if none
	 */
	public double max() {
		return _max;
	}

	/**
	 * Return the sum of values recorded, or zero if no values have been
	 * recorded.
	 *
	 * @return the sum of values, or zero if none
	 */
	public double sum() {
		return _sum.doubleValue();
	}

	/**
	 * Compares the state of two {@code DoubleMomentStatistics} objects. This is
	 * a replacement for the {@link #equals(Object)} which is not advisable to
	 * implement for this mutable object. If two objects have the same state, it
	 * has still the same state when updated with the same value.
	 * {@snippet lang="java":
	 * final DoubleMomentStatistics ds1 = null; // @replace substring='null' replacement="..."
	 * final DoubleMomentStatistics ds2 = null; // @replace substring='null' replacement="..."
	 *
	 * if (ds1.sameState(ds2)) {
	 *     final double value = random.nextDouble();
	 *     ds1.accept(value);
	 *     ds2.accept(value);
	 *
	 *     assert ds1.sameState(ds2);
	 *     assert ds2.sameState(ds1);
	 *     assert ds1.sameState(ds1);
	 * }
	 * }
	 *
	 * @since 3.7
	 *
	 * @param other the other object for the test
	 * @return {@code true} the {@code this} and the {@code other} objects have
	 *         the same state, {@code false} otherwise
	 */
	public boolean sameState(final DoubleMomentStatistics other) {
		return this == other ||
			Double.compare(_min, other._min) == 0 &&
			Double.compare(_max, other._max) == 0 &&
			_sum.sameState(other._sum) &&
			super.sameState(other);
	}

	/**
	 * Return a {@code DoubleMoments} object from the current statistics,
	 *
	 * @since 3.9
	 *
	 * @return a {@code DoubleMoments} object from the current statistics
	 */
	public DoubleMoments toDoubleMoments() {
		return DoubleMoments.of(this);
	}

	public DoubleMoments result() {
		return toDoubleMoments();
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			count(), _min, _max, _sum.doubleValue(),
			mean(), variance(), skewness(), kurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies a double-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * {@snippet lang="java":
	 * final Stream<SomeObject> stream = null; // @replace substring='null' replacement="..."
	 * final DoubleMomentStatistics statistics = stream
	 *     .collect(toDoubleMomentStatistics(v -> v.doubleValue()));
	 * }
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the moments-statistics reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, DoubleMomentStatistics>
	toDoubleMomentStatistics(final ToDoubleFunction<? super T> mapper) {
		requireNonNull(mapper);
		return Collector.of(
			DoubleMomentStatistics::new,
			(r, t) -> r.accept(mapper.applyAsDouble(t)),
			DoubleMomentStatistics::combine
		);
	}

}
