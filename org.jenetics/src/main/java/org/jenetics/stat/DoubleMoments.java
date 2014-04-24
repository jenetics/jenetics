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

import java.util.Objects;
import java.util.function.DoubleConsumer;

import org.jenetics.internal.math.DoubleAdder;

/**
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-24 $</em>
 * @since 3.0
 */
public class DoubleMoments extends MomentsBase implements DoubleConsumer {

	private double _min = Double.POSITIVE_INFINITY;
	private double _max = Double.NEGATIVE_INFINITY;

	private final DoubleAdder _sum = new DoubleAdder();

	@Override
	public void accept(final double value) {
		update(value);

		_min = min(_min, value);
		_max = max(_max, value);
		_sum.add(value);
	}

	/**
	 * Combine two {@code DoubleMoments} statistic objects.
	 *
	 * @param other the other {@code DoubleMoments} statistics to combine with
	 *        {@code this} one.
	 * @return a new statistical objects.
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public DoubleMoments combine(final DoubleMoments other) {
		Objects.requireNonNull(other);

		final DoubleMoments result = new DoubleMoments();
		MomentsBase.combine(this, other, result);

		result._min = min(_min, other._min);
		result._max = max(_max, other._max);
		result._sum.set(_sum).add(other._sum);

		return result;
	}

	public double getMin() {
		return _min;
	}

	public double getMax() {
		return _max;
	}

	public double getSum() {
		return _sum.doubleValue();
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s2=%s, S=%s, K=%s]",
			getCount(), _min, _max, _sum.doubleValue(),
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}

}
