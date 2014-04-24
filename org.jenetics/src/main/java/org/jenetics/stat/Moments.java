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

import org.jenetics.internal.math.DoubleAdder;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date$</em>
 * @since 3.0
 */
class Moments {

	// the sample count.
	private long _n = 0L;

	// Variables used for statistical moments.
	private final DoubleAdder _m1 = new DoubleAdder();
	private final DoubleAdder _m2 = new DoubleAdder();
	private final DoubleAdder _m3 = new DoubleAdder();
	private final DoubleAdder _m4 = new DoubleAdder();


	void update(final double value) {
		++_n;

		final double n = _n;
		final double d = value - _m1.doubleValue();
		final double dN = d/n;
		final double dN2 = dN*dN;
		final double t1 = d*dN*(n - 1.0);

		_m1.add(dN);
		_m4.add(t1*dN2*(n*n - 3.0*n + 3.0))
			.add(6.0 * dN2 * _m2.doubleValue() - 4.0 * dN * _m3.doubleValue());
		_m3.add(t1*dN*(n - 2.0) - 3.0*dN*_m2.doubleValue());
		_m2.add(t1);
	}

	/**
	 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
	 *      Computing Higher-Order Moments Online</a>
	 */
	static void combine(
		final Moments a,
		final Moments b,
		final Moments r
	) {
		final double d = b._m1.doubleValue() - a._m1.doubleValue();
		final double d2 = d*d;
		final double d3 = d2*d;
		final double d4 = d3*d;

		r._n = a._n + b._n;

		r._m1.set(a._m1).add(d*b._n/(double)r._n);

		r._m2.set(a._m2).add(b._m2)
			.add(d2*a._n*b._n/(double)r._n);

		r._m3.set(a._m3).add(b._m3)
			.add(d3*(a._n*b._n*(a._n - b._n)/(r._n*r._n)))
			.add(3.0*d*(a._n*b._m2.doubleValue() - b._n*a._m2.doubleValue())/r._n);

		r._m4.set(a._m4).add(b._m4)
			.add(d4*(a._n*b._n*(a._n*a._n - a._n*b._n + b._n*b._n)/(r._n*r._n*r._n)))
			.add(6.0*d*d*(a._n*a._n*b._m2.doubleValue() +
						b._n*b._n*a._m2.doubleValue())/(r._n*r._n))
			.add(4.0*d*(a._n*b._m3.doubleValue() - b._n*a._m3.doubleValue())/r._n);
	}

	public long getCount() {
		return _n;
	}

	public double getMean() {
		return _n == 0L ? NaN : _m1.doubleValue();
	}

	public double getVariance() {
		double var = NaN;
		if (_n == 1L) {
			var = _m2.doubleValue();
		} else if (_n > 1L) {
			var = _m2.doubleValue()/(_n - 1.0);
		}

		return var;
	}

	public double getSkewness() {
		double skewness = NaN;
		if (_n >= 3L) {
			final double var = _m2.doubleValue()/(_n - 1.0);
			if (var < 10E-20) {
				skewness = 0.0d;
			} else {
				skewness = (_n*_m3.doubleValue())/((_n - 1.0)*(_n - 2.0)*sqrt(var)*var);
			}
		}

		return skewness;
	}

	public double getKurtosis() {
		double kurtosis = NaN;
		if (_n > 3L) {
			final double var = _m2.doubleValue()/(_n - 1);
			if (_n <= 3L || var < 10E-20) {
				kurtosis = 0.0;
			} else {
				kurtosis = (_n*(_n + 1.0)*_m4.doubleValue() -
					3.0*_m2.doubleValue()*_m2.doubleValue()*(_n - 1.0))/
					((_n - 1.0)*(_n - 2.0)*(_n - 3.0)*var*var);
			}
		}
		return kurtosis;
	}

}
