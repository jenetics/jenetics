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

import org.jenetics.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-22 $</em>
 * @since 3.0
 */
class MomentsBase {

	// the sample count.
	long n = 0L;

	// The accumulated sum.
	double sum = 0.0;

	// Used for the Kahan summation algorithm.
	double c = 0.0;

	// Variables used for statistical moments.
	double m1 = 0.0;
	double m2 = 0.0;
	double m3 = 0.0;
	double m4 = 0.0;

	void updateSum(final double value) {
		final double y = value - c;
		final double t = sum + y;
		c = t - sum - y;
		sum = t;
	}

	void updateMoments(final double value) {
		final double d = value - m1;
		final double dN = d/ n;
		final double dN2 = dN*dN;
		final double t1 = d*dN*(n - 1.0);
		m1 += dN;
		m4 += t1*dN2 *(n * n - 3.0* n + 3.0) + 6.0*dN2* m2 - 4.0*dN* m3;
		m3 += t1*dN*(n - 2.0) - 3.0*dN* m2;
		m2 += t1;
	}

	/**
	 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
	 *      Computing Higher-Order Moments Online</a>
	 */
	void combineMoments(final MomentsBase b, final MomentsBase r) {
		final double d = b.m1 - m1;
		final double d2 = d*d;
		final double d3 = d2*d;
		final double d4 = d3*d;

		r.n = n + b.n;
		r.m1 = m1 + d*b.n/(double)r.n;
		r.m2 = m2 + b.m2 + d2* n *b.n /(double)r.n;
		r.m3 = m3 + b.m3 + d3*(n *b.n *(n - b.n)/(r.n *r.n)) +
			3*d*(n *b.m2 - b.n * m2)/r.n;
		r.m4 = m4 + b.m4 + d4*(
			n *b.n *(n * n - n *b.n + b.n *b.n)/(r.n *r.n *r.n)
		) +
			6.0*d*d*(n * n *b.m2 + b.n *b.n * m2)/(r.n *r.n) +
			4.0*d*(n *b.m3 - b.n * m3)/r.n;
	}

	public long getCount() {
		return n;
	}

	public double getSum() {
		return sum;
	}

	public double getMean() {
		return n == 0 ? NaN : m1;
	}

	public double getVariance() {
		double var = NaN;
		if (n == 1) {
			var = m2;
		} else if (n > 1) {
			var = m2 /(n - 1);
		}

		return var;
	}

	public double getSkewness() {
		double skewness = NaN;
		if (n >= 3) {
			final double var = m2 /(n - 1);
			if (var < 10E-20) {
				skewness = 0.0d;
			} else {
				skewness = (n * m3) /((n - 1.0)*(n - 2.0)*sqrt(var)*var);
			}
		}

		return skewness;
	}

	public double getKurtosis() {
		double kurtosis = NaN;
		if (n > 3) {
			final double var = m2 /(n - 1);
			if (n <= 3 || var < 10E-20) {
				kurtosis = 0.0;
			} else {
				kurtosis = (n * (n + 1)* m4 -
					3* m2 * m2 *(n - 1))/
					((n - 1)*(n -2)*(n -3)*var*var);
			}
		}
		return kurtosis;
	}

	@Override
	public int hashCode() {
		return Hash.of(Moments.class)
			.and(n)
			.and(sum)
			.and(m1)
			.and(m2)
			.and(m3)
			.and(m4).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof MomentsBase)) {
			return false;
		}

		final MomentsBase sum = (MomentsBase)obj;
		return eq(n, sum.n) &&
			eq(this.sum, sum.sum) &&
			eq(m1, sum.m1) &&
			eq(m2, sum.m2) &&
			eq(m3, sum.m3) &&
			eq(m4, sum.m4);
	}

}
