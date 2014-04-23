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
 * @version 3.0 &mdash; <em>$Date: 2014-04-23 $</em>
 * @since 3.0
 */
class MomentsBase {

	// the sample count.
	long n = 0L;

	// Variables used for statistical moments.
	final DoubleAdder m1 = new DoubleAdder();
	final DoubleAdder m2 = new DoubleAdder();
	final DoubleAdder m3 = new DoubleAdder();
	final DoubleAdder m4 = new DoubleAdder();

	void update(final double value) {
		++n;

		final double d = value - m1.value;
		final double dN = d/ n;
		final double dN2 = dN*dN;
		final double t1 = d*dN*(n - 1.0);

		m1.add(dN);
		m4.add(t1*dN2*(n*n - 3.0*n + 3.0)).add(6.0 * dN2 * m2.value - 4.0 * dN * m3.value);
		m3.add(t1*dN*(n - 2.0) - 3.0*dN*m2.value);
		m2.add(t1);
	}

	/**
	 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
	 *      Computing Higher-Order Moments Online</a>
	 */
	static <M extends MomentsBase> M combine(final M a, final M b, final M r) {
		final double d = b.m1.value - a.m1.value;
		final double d2 = d*d;
		final double d3 = d2*d;
		final double d4 = d3*d;

		r.n = a.n + b.n;

		r.m1.set(a.m1).add(d*b.n/(double)r.n);

		r.m2.set(a.m2).add(b.m2)
			.add(d2*a.n *b.n/(double)r.n);

		r.m3.set(a.m3).add(b.m3)
			.add(d3*(a.n*b.n*(a.n - b.n)/(r.n*r.n)))
			.add(3*d*(a.n*b.m2.value - b.n*a.m2.value)/r.n);

		r.m4.set(a.m4).add(b.m4)
			.add(d4*(a.n*b.n*(a.n*a.n - a.n*b.n + b.n*b.n)/(r.n*r.n*r.n)))
			.add(6.0*d*d*(a.n*a.n*b.m2.value + b.n*b.n*a.m2.value)/(r.n*r.n))
			.add(4.0*d*(a.n*b.m3.value - b.n*a.m3.value)/r.n);

		return r;
	}

	public long getCount() {
		return n;
	}

	public double getMean() {
		return n == 0 ? NaN : m1.value;
	}

	public double getVariance() {
		double var = NaN;
		if (n == 1) {
			var = m2.value;
		} else if (n > 1) {
			var = m2.value/(n - 1);
		}

		return var;
	}

	public double getSkewness() {
		double skewness = NaN;
		if (n >= 3) {
			final double var = m2.value/(n - 1);
			if (var < 10E-20) {
				skewness = 0.0d;
			} else {
				skewness = (n*m3.value)/((n - 1.0)*(n - 2.0)*sqrt(var)*var);
			}
		}

		return skewness;
	}

	public double getKurtosis() {
		double kurtosis = NaN;
		if (n > 3) {
			final double var = m2.value/(n - 1);
			if (n <= 3 || var < 10E-20) {
				kurtosis = 0.0;
			} else {
				kurtosis = (n*(n + 1.0)*m4.value -
					3*m2.value*m2.value*(n - 1.0))/
					((n - 1.0)*(n - 2.0)*(n - 3.0)*var*var);
			}
		}
		return kurtosis;
	}

}
