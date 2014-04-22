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

import static java.lang.Double.NaN;
import static java.lang.Math.sqrt;
import static org.jenetics.internal.util.object.eq;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date$</em>
 * @since 3.0
 */
public class Moments<N extends Number & Comparable<? super N>>
	implements Consumer<N>
{

	private long _n = 0L;
	private N _min = null;
	private N _max = null;

	private double _sum = 0.0;
	// Used for the Kahan summation algorithm.
	private double _c = 0.0;

	// Variables used for statistical moments.
	private double _m1 = 0.0;
	private double _m2 = 0.0;
	private double _m3 = 0.0;
	private double _m4 = 0.0;

	@Override
	public void accept(final N number) {
		final double value = number.doubleValue();

		if (_min == null || _min.compareTo(number) > 0) _min = number;
		if (_max == null || _max.compareTo(number) < 0) _max = number;
		++_n;
		combineSum(value);
		combineMoments(value);
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

		result._n = _n + other._n;
		result._min = _min.compareTo(other._min) < 0 ? _min : other._min;
		result._max = _max.compareTo(other._max) > 0 ? _max : other._max;
		result.combineSum(_sum);
		result.combineSum(other._sum);
		combineMoments(other, result);

		return result;
	}

	private void combineSum(final double value) {
		final double y = value - _c;
		final double t = _sum + y;
		_c = t - _sum - y;
		_sum = t;
	}

	private void combineMoments(final double value) {
		final double d = value - _m1;
		final double dN = d/_n;
		final double dN2 = dN*dN;
		final double t1 = d*dN*(_n - 1.0);
		_m1 += dN;
		_m4 += t1*dN2 *(_n*_n - 3.0*_n + 3.0) + 6.0*dN2*_m2 - 4.0*dN*_m3;
		_m3 += t1*dN*(_n - 2.0) - 3.0*dN*_m2;
		_m2 += t1;
	}

	/**
	 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
	 *      Computing Higher-Order Moments Online</a>
	 */
	private void combineMoments(
		final Moments<N> b,
		final Moments<N> r
	) {
		final double d = b._m1 - _m1;
		final double d2 = d*d;
		final double d3 = d2*d;
		final double d4 = d3*d;

		r._n = _n + b._n;

		r._m1 = _m1 + d*b._n /(double)r._n;

		r._m2 = _m2 + b._m2 + d2* _n *b._n /(double)r._n;

		r._m3 = _m3 + b._m3 + d3*(_n*b._n*(_n - b._n)/(r._n*r._n)) +
				3*d*(_n*b._m2 - b._n*_m2)/r._n;

		r._m4 = _m4 + b._m4 + d4*(
					_n*b._n*(_n*_n - _n*b._n + b._n*b._n)/(r._n*r._n*r._n)
				) +
				6.0*d*d*(_n*_n*b._m2 + b._n*b._n*_m2)/(r._n*r._n) +
				4.0*d*(_n*b._m3 - b._n*_m3)/r._n;
	}

	public long getCount() {
		return _n;
	}

	public N getMin() {
		return _min;
	}

	public N getMax() {
		return _max;
	}

	public double getSum() {
		return _sum;
	}

	public double getMean() {
		return _n == 0 ? NaN : _m1;
	}

	public double getVariance() {
		double var = NaN;
		if (_n == 1) {
			var = _m2;
		} else if (_n > 1) {
			var = _m2/(_n - 1);
		}

		return var;
	}

	public double getSkewness() {
		double skewness = NaN;
		if (_n >= 3) {
			final double var = _m2/(_n - 1);
			if (var < 10E-20) {
				skewness = 0.0d;
			} else {
				skewness = (_n*_m3) /((_n - 1.0)*(_n - 2.0)*sqrt(var)*var);
			}
		}

		return skewness;
	}

	public double getKurtosis() {
		double kurtosis = NaN;
		if (_n > 3) {
			final double var = _m2/(_n - 1);
			if (_n <= 3 || var < 10E-20) {
				kurtosis = 0.0;
			} else {
				kurtosis = (_n * (_n + 1)*_m4 -
							3*_m2*_m2*(_n - 1))/
								((_n - 1)*(_n -2)*(_n -3)*var*var);
			}
		}
		return kurtosis;
	}

	public int hashCode() {
		return Hash.of(Moments.class)
			.and(_n)
			.and(_min)
			.and(_max)
			.and(_sum)
			.and(_m1)
			.and(_m2)
			.and(_m3)
			.and(_m4).value();
	}

	public boolean equals(final Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final Moments<?> sum = (Moments<?>)obj;
		return eq(_n, sum._n) &&
				eq(_min, sum._min) &&
				eq(_max, sum._max) &&
				eq(_sum, sum._sum) &&
				eq(_m1, sum._m1) &&
				eq(_m2, sum._m2) &&
				eq(_m3, sum._m3) &&
				eq(_m4, sum._m4);
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s2=%s, S=%s, K=%s]",
			_n, _min, _max, _sum,
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
