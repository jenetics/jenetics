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
 * @version @__version__@ &mdash; <em>$Date: 2013-11-13 $</em>
 * @since @__version__@
 */
final class CollectibleSummary<N extends Number & Comparable<? super N>>
	implements Summary<N>
{

	private long _samples = 0L;
	private N _min;
	private N _max;

	// Sum variables which are used for the Kahan summation algorithm.
	private double _sum = 0.0;
	private double _c = 0.0;
	private double _y = 0.0;
	private double _t = 0.0;

	private double _mean = NaN;
	private double _skewness = NaN;
	private double _kurtosis = NaN;

	// Helper variable for calculating the variance.
	private double _m2 = NaN;

	void accumulate(final N number) {
		final double value = number.doubleValue();

		++_samples;
		updateMin(number);
		updateMax(number);
		updateSum(value);
		updateMoment(value);
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

	public void updateMoment(final double value) {
		if (_samples == 1) {
			_mean = 0;
			_m2 = 0;
		}

		final double data = value;
		final double delta = data - _mean;

		_mean += delta/_samples;
		_m2 += delta*(data - _mean);
	}

	CollectibleSummary<N> combine(final CollectibleSummary<N> other) {
		final CollectibleSummary<N> result = new CollectibleSummary<>();

		result._samples = _samples + other._samples;
		result._min = _min.compareTo(other._min) < 0 ? _min : other._min;
		result._max = _max.compareTo(other._max) > 0 ? _max : other._max;
		result.updateSum(_sum);
		result.updateSum(other._sum);

		combineVariance(other, result);
		return result;
	}

	private void combineVariance(
		final CollectibleSummary<N> other,
		final CollectibleSummary<N> result
	) {
		final double delta = other._mean - _mean;
		result._samples = _samples + other._samples;
		result._mean = _mean + delta*other._samples/(double)result._samples;

		result._m2 = _m2 + other._m2 +
			delta*delta*_samples*other._samples/(double)result._samples;
	}

	@Override
	public long getSampleSize() {
		return _samples;
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

		if (_samples == 1) {
			variance = _m2;
		} else if (_samples > 1) {
			variance = _m2/(_samples - 1);
		}

		return variance;
	}

	@Override
	public double getSkewness() {
		return _skewness;
	}

	@Override
	public double getKurtosis() {
		return _kurtosis;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(CollectibleSummary.class)
			.and(_samples)
			.and(_min)
			.and(_max)
			.and(_sum)
			.and(_mean)
			.and(_m2)
			.and(_skewness)
			.and(_kurtosis).value();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}

		final CollectibleSummary sum = (CollectibleSummary)other;
		return eq(_samples, sum._samples) &&
				eq(_min, sum._min) &&
				eq(_max, sum._max) &&
				eq(_sum, sum._sum) &&
				eq(_mean, sum._mean) &&
				eq(_m2, sum._m2) &&
				eq(_skewness, sum._skewness) &&
				eq(_kurtosis, sum._kurtosis);
	}

}
