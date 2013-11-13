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
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-11-14 $</em>
 * @since @__version__@
 */
final class CollectibleSummary<N extends Number & Comparable<? super N>>
	implements Summary<N>
{

	private long _n = 0L;
	private N _min;
	private N _max;

	// Sum variables which are used for the Kahan summation algorithm.
	private double _sum = 0.0;
	private double _c = 0.0;
	private double _y = 0.0;
	private double _t = 0.0;

	private double _mean = NaN;
	private double _m2 = NaN;
	private double _m3 = NaN;
	private double _m4 = NaN;

	void accumulate(final N number) {
		final double value = number.doubleValue();

		updateMin(number);
		updateMax(number);
		updateSum(value);
		updateMoments(value);
	}

	private void updateMin(final N number) {
		if (_min == null || _min.compareTo(number) > 0) {
			_min = number;
		}
	}

	private void updateMax(final N number) {
		if (_max == null || _max.compareTo(number) < 0) {
			_max = number;
		}
	}

	private void updateSum(final double number) {
		_y = number - _c;
		_t = _sum + _y;
		_c = _t - _sum - _y;
		_sum = _t;
	}

	private void updateMoments(final double value) {

		if (_n == 0) {
			_mean = 0;
			_m2 = 0;
			_m3 = 0;
			_m4 = 0;
		}
/*
		final double data = value;
		final double delta = data - _mean;

		_mean += delta/ _n;
		_m2 += delta*(data - _mean);
*/

		final double n1 = _n;
		++_n;
		final double delta = value - _mean;
		final double deltaN = delta/_n;
		final double deltaN2 = deltaN*deltaN;
		final double term1 = delta*deltaN*n1;
		_mean += deltaN;
		_m4 += term1*deltaN2 *(_n*_n - 3*_n + 3) + 6*deltaN2*_m2 - 4*deltaN*_m3;
		_m3 += term1*deltaN*(_n - 2) - 3*deltaN*_m2;
		_m2 += term1;
	}

/*
	def online_kurtosis(data):
	n = 0
	mean = 0
	M2 = 0
	M3 = 0
	M4 = 0

	for x in data:
		n1 = n
		n = n + 1
		delta = x - mean
		delta_n = delta / n
		delta_n2 = delta_n * delta_n
		term1 = delta * delta_n * n1
		mean = mean + delta_n
		M4 = M4 + term1 * delta_n2 * (n*n - 3*n + 3) + 6 * delta_n2 * M2 - 4 * delta_n * M3
		M3 = M3 + term1 * delta_n * (n - 2) - 3 * delta_n * M2
		M2 = M2 + term1

	kurtosis = (n*M4) / (M2*M2) - 3
	return kurtosis
*/
	CollectibleSummary<N> combine(final CollectibleSummary<N> other) {
		final CollectibleSummary<N> result = new CollectibleSummary<>();

		result._n = _n + other._n;
		result._min = _min.compareTo(other._min) < 0 ? _min : other._min;
		result._max = _max.compareTo(other._max) > 0 ? _max : other._max;
		result.updateSum(_sum);
		result.updateSum(other._sum);

		combineMoments(other, result);
		return result;
	}

	// http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
	private void combineMoments(
		final CollectibleSummary<N> other,
		final CollectibleSummary<N> result
	) {
		final double delta = other._mean - _mean;
		result._n = _n + other._n;
		result._mean = _mean + delta*other._n /(double)result._n;

		result._m2 = _m2 + other._m2 +
			delta*delta* _n *other._n /(double)result._n;

		result._m3 = _m3 + other._m3 +
			delta*delta*delta*(
				_n*other._n*(_n - other._n)/(result._n*result._n)
			) +
			3*delta*(_n*other._m2 - other._n*_m2)/result._n;

		result._m4 = _m4 + other._m4 +
			delta*delta*delta*delta*(
				_n*other._n*(_n*_n - _n*other._n + other._n*other._n)/(result._n*result._n*result._n)
			) +
			6*delta*delta*(_n*_n*other._m2 + other._n*other._n*_m2)/(result._n*result._n) +
			4*delta*(_n*other._m3 - other._n*_m3)/result._n;
	}

	@Override
	public long getSampleSize() {
		return _n;
	}

	@Override
	public N getMin() {
		return _min;
	}

	@Override
	public N getMax() {
		return _max;
	}

	@Override
	public double getSum() {
		return _sum;
	}

	@Override
	public double getMean() {
		return _mean;
	}

	@Override
	public double getVariance() {
		double variance = NaN;

		if (_n == 1) {
			variance = _m2;
		} else if (_n > 1) {
			variance = _m2/(_n - 1);
		}

		return variance;
	}

	@Override
	public double getSkewness() {
		if (_n < 3) {
			return Double.NaN;
		}

		double variance = _m2/(_n - 1);
		if (variance < 10E-20) {
			return 0.0d;
		} else {
			double n0 = _n;
			return (n0*_m3) /
				((n0 - 1)*(n0 -2)*Math.sqrt(variance)*variance);
		}
	}

	@Override
	public double getKurtosis() {
		//return (_n*_m4)/(_m2*_m2) - 3;
		double kurtosis = Double.NaN;
		if (_n > 3) {
			final double variance = _m2/(_n - 1);
			if (_n <= 3 || variance < 10E-20) {
				kurtosis = 0.0;
			} else {
				kurtosis =
					(_n * (_n + 1)*_m4 -
						3*_m2*_m2*(_n - 1))/
						((_n - 1)*(_n -2)*(_n -3)*variance*variance);
			}
		}
		return kurtosis;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(CollectibleSummary.class)
			.and(_n)
			.and(_min)
			.and(_max)
			.and(_sum)
			.and(_mean)
			.and(_m2)
			.and(_m3)
			.and(_m4).value();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}

		final CollectibleSummary sum = (CollectibleSummary)other;
		return eq(_n, sum._n) &&
				eq(_min, sum._min) &&
				eq(_max, sum._max) &&
				eq(_sum, sum._sum) &&
				eq(_mean, sum._mean) &&
				eq(_m2, sum._m2) &&
				eq(_m3, sum._m3) &&
				eq(_m4, sum._m4);
	}

}
