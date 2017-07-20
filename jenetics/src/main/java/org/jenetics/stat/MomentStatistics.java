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
import static java.util.Objects.requireNonNull;

import org.jenetics.internal.math.DoubleAdder;

/**
 * Base class for statistical moments calculation.
 *
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.1
 */
abstract class MomentStatistics {

	// the sample count.
	private long _n = 0L;

	// Variables used for statistical moments.
	private final DoubleAdder _m1 = new DoubleAdder();
	private final DoubleAdder _m2 = new DoubleAdder();
	private final DoubleAdder _m3 = new DoubleAdder();
	private final DoubleAdder _m4 = new DoubleAdder();


	/**
	 * Update the moments with the given {@code value}.
	 *
	 * @param value the value which is used to update this statistical moments.
	 */
	void accept(final double value) {
		++_n;

		final double n = _n;
		final double d = value - _m1.value();
		final double dN = d/n;
		final double dN2 = dN*dN;
		final double t1 = d*dN*(n - 1.0);

		_m1.add(dN);
		_m4.add(t1*dN2*(n*n - 3.0*n + 3.0))
			.add(6.0*dN2*_m2.value() - 4.0*dN*_m3.value());
		_m3.add(t1*dN*(n - 2.0) - 3.0*dN*_m2.value());
		_m2.add(t1);
	}

	/**
	 * Combines the state of another {@code Moments} object into this one.
	 *
	 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
	 *      Computing Higher-Order Moments Online</a>
	 */
	void combine(final MomentStatistics b) {
		requireNonNull(b);

		final double m2 = _m2.value();
		final double m3 = _m3.value();

		final double pn = _n;
		final double n = _n + b._n;
		final double nn = n*n;

		final double d = b._m1.value() - _m1.value();
		final double dd = d*d;

		_n += b._n;

		_m1.add(d*b._n/n);

		_m2.add(b._m2).add(dd*pn*b._n/n);

		_m3.add(b._m3)
			.add(dd*d*(pn*b._n*(pn - b._n)/nn))
			.add(3.0*d*(pn*b._m2.value() - b._n*m2)/n);

		_m4.add(b._m4)
			.add(dd*dd*(pn*b._n*(pn*pn - pn*b._n + b._n*b._n)/(nn*n)))
			.add(6.0*dd*(pn*pn*b._m2.value() + b._n*b._n*m2)/nn)
			.add(4.0*d*(pn*b._m3.value() - b._n*m3)/n);
	}

	/**
	 * Returns the count of values recorded.
	 *
	 * @return the count of recorded values
	 */
	public long getCount() {
		return _n;
	}

	/**
	 * Return the arithmetic mean of values recorded, or {@code Double.NaN} if
	 * no values have been recorded.
	 *
	 * @return the arithmetic mean of values, or zero if none
	 */
	public double getMean() {
		return _n == 0L ? NaN : _m1.value();
	}

	/**
	 * Return the variance of values recorded, or {@code Double.NaN} if no
	 * values have been recorded.
	 *
	 * @return the variance of values, or {@code NaN} if none
	 */
	public double getVariance() {
		double var = NaN;
		if (_n == 1L) {
			var = _m2.value();
		} else if (_n > 1L) {
			var = _m2.value()/(_n - 1.0);
		}

		return var;
	}

	/**
	 * Return the skewness of values recorded, or {@code Double.NaN} if less
	 * than two values have been recorded.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Skewness">Skewness</a>
	 *
	 * @return the skewness of values, or {@code NaN} if less than two values
	 *         have been recorded
	 */
	public double getSkewness() {
		double skewness = NaN;
		if (_n >= 3L) {
			final double var = _m2.value()/(_n - 1.0);
			if (var < 10E-20) {
				skewness = 0.0d;
			} else {
				skewness = (_n*_m3.value())/
						((_n - 1.0)*(_n - 2.0)*sqrt(var)*var);
			}
		}

		return skewness;
	}

	/**
	 * Return the kurtosis of values recorded, or {@code Double.NaN} if less
	 * than four values have been recorded.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Kurtosis">Kurtosis</a>
	 *
	 * @return the kurtosis of values, or {@code NaN} if less than four values
	 *         have been recorded
	 */
	public double getKurtosis() {
		double kurtosis = NaN;
		if (_n > 3L) {
			final double var = _m2.value()/(_n - 1);
			if (_n <= 3L || var < 10E-20) {
				kurtosis = 0.0;
			} else {
				kurtosis = (_n*(_n + 1.0)*_m4.value() -
					3.0*_m2.value()*_m2.value()*(_n - 1.0))/
					((_n - 1.0)*(_n - 2.0)*(_n - 3.0)*var*var);
			}
		}
		return kurtosis;
	}

	final boolean sameState(final MomentStatistics statistics) {
		return _n == statistics._n &&
			_m1.sameState(statistics._m1) &&
			_m2.sameState(statistics._m2) &&
			_m3.sameState(statistics._m3) &&
			_m4.sameState(statistics._m4);
	}

}
