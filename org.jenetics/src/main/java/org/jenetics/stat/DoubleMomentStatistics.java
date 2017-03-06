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

import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import org.jenetics.internal.math.DoubleAdder;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * {@link java.util.DoubleSummaryStatistics} class.
 * <p>
 * This class is designed to work with (though does not require) streams. For
 * example, you can compute moments-statistics on a stream of doubles with:
 * <pre>{@code
 * final DoubleStream stream = ...
 * final DoubleMomentStatistics statistics = stream.collect(
 *         DoubleMomentStatistics::new,
 *         DoubleMomentStatistics::accept,
 *         DoubleMomentStatistics::combine
 *     );
 * }</pre>
 *
 * For a non double stream, you can use a collector:
 * <pre>{@code
 * final Stream<SomeObject> stream = ...
 * final DoubleMomentStatistics statistics = stream
 *     .collect(toDoubleMomentStatistics(v -> v.doubleValue()));
 * }</pre>
 *
 * <p>
 * <b>Implementation note:</b>
 * <i>This implementation is not thread safe. However, it is safe to use
 * {@link #toDoubleMomentStatistics(ToDoubleFunction)}  on a parallel stream,
 * because the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.</i>
 *
 * @see java.util.DoubleSummaryStatistics
 * @see org.jenetics.stat.DoubleMoments
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.7
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
	 * Records a new value into the moments information
	 *
	 * @param value the input {@code value}
	 */
	@Override
	public void accept(final double value) {
		super.accept(value);
		_min = min(_min, value);
		_max = max(_max, value);
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
		_min = min(_min, other._min);
		_max = max(_max, other._max);
		_sum.add(other._sum);

		return this;
	}

	/**
	 * Return the minimum value recorded, or {@code Double.POSITIVE_INFINITY} if
	 * no values have been recorded.
	 *
	 * @return the minimum value, or {@code Double.POSITIVE_INFINITY} if none
	 */
	public double getMin() {
		return _min;
	}

	/**
	 * Return the maximum value recorded, or {@code Double.NEGATIVE_INFINITY} if
	 * no values have been recorded.
	 *
	 * @return the maximum value, or {@code Double.NEGATIVE_INFINITY} if none
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
		return _sum.doubleValue();
	}

	/**
	 * Compares the state of two {@code DoubleMomentStatistics} objects. This is
	 * a replacement for the {@link #equals(Object)} which is not advisable to
	 * implement for this mutable object. If two object have the same state, it
	 * has still the same state when updated with the same value.
	 * <pre>{@code
	 * final DoubleMomentStatistics ds1 = ...;
	 * final DoubleMomentStatistics ds2 = ...;
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
	 * }</pre>
	 *
	 * @since 3.7
	 *
	 * @param other the other object for the test
	 * @return {@code true} the {@code this} and the {@code other} objects have
	 *         the same state, {@code false} otherwise
	 */
	public boolean sameState(final DoubleMomentStatistics other) {
		return Double.compare(_min, other._min) == 0 &&
			Double.compare(_max, other._max) == 0 &&
			_sum.sameState(other._sum) &&
			super.sameState(other);
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s²=%s, S=%s, K=%s]",
			getCount(), _min, _max, _sum.doubleValue(),
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}

	/**
	 * Return a {@code Collector} which applies an double-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final DoubleMomentStatistics statistics = stream
	 *     .collect(toDoubleMomentStatistics(v -> v.doubleValue()));
	 * }</pre>
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
