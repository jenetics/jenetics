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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

/**
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-23 $</em>
 * @since 3.0
 */
public class Moments<N extends Number & Comparable<? super N>>
	extends MomentsBase
	implements Consumer<N>
{

	private N _min = null;
	private N _max = null;

	@Override
	public void accept(final N number) {
		final double value = number.doubleValue();

		if (_min == null || _min.compareTo(number) > 0) _min = number;
		if (_max == null || _max.compareTo(number) < 0) _max = number;
		++n;
		updateSum(value);
		updateMoments(value);
	}

	/**
	 * Combine two {@code Moments} statistic objects.
	 *
	 * @param other the other {@code Moments} statistics to combine with
	 *        {@code this} one.
	 * @return a new statistical objects.
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public Moments<N> combine(final Moments<N> other) {
		Objects.requireNonNull(other);
		final Moments<N> result = new Moments<>();

		result.n = n + other.n;
		result._min = _min.compareTo(other._min) < 0 ? _min : other._min;
		result._max = _max.compareTo(other._max) > 0 ? _max : other._max;
		result.updateSum(sum);
		result.updateSum(other.sum);
		combineMoments(other, result);

		return result;
	}

	public N getMin() {
		return _min;
	}

	public N getMax() {
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

	public static <N extends Number & Comparable<? super N>>
	Collector<N, ?, Moments<N>> collector() {
		return Collector.of(
			Moments::new,
			Moments::accept,
			Moments::combine
		);
	}

}
