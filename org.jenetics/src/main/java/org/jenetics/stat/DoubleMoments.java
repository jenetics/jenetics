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

import static org.jenetics.internal.util.object.eq;

import java.util.Objects;
import java.util.function.DoubleConsumer;

import org.jenetics.internal.util.Hash;

/**
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-22 $</em>
 * @since 3.0
 */
public class DoubleMoments extends MomentsBase implements DoubleConsumer {

	private double _min = Double.POSITIVE_INFINITY;
	private double _max = Double.NEGATIVE_INFINITY;

	@Override
	public void accept(final double value) {
		_min = Math.min(_min, value);
		_max = Math.max(_max, value);

		++n;
		updateSum(value);
		updateMoments(value);
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

		result.n = n + other.n;
		result._min = Math.min(_min, other._min);
		result._max = Math.max(_max, other._max);
		result.updateSum(sum);
		result.updateSum(other.sum);
		combineMoments(other, result);

		return result;
	}

	public double getMin() {
		return _min;
	}

	public double getMax() {
		return _max;
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s2=%s, S=%s, K=%s]",
			n, _min, _max, sum,
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}

	@Override
	public int hashCode() {
		return Hash.of(DoubleMoments.class)
			.and(super.hashCode())
			.and(_min)
			.and(_max).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DoubleMoments)) {
			return false;
		}

		final DoubleMoments sum = (DoubleMoments)obj;
		return super.equals(obj) &&
			eq(_min, sum._min) &&
			eq(_max, sum._max);
	}

}
