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
package org.jenetics.trial;

import java.io.Serializable;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SampleSummaryPoint implements Serializable {
	private static final long serialVersionUID = 1L;

	public static SampleSummaryPoint NaN = of(
		Double.NaN,
		Double.NaN,
		Double.NaN,
		Double.NaN,
		Double.NaN,
		Double.NaN,
		Double.NaN,
		Double.NaN,
		Double.NaN
	);

	private final double _mean;
	private final double _variance;
	private final double _skewness;
	private final double _kurtosis;
	private final double _median;
	private final double _low;
	private final double _high;
	private final double _min;
	private final double _max;

	private SampleSummaryPoint(
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis,
		final double median,
		final double low,
		final double high,
		final double min,
		final double max
	) {
		_mean = mean;
		_variance = variance;
		_skewness = skewness;
		_kurtosis = kurtosis;
		_median = median;
		_low = low;
		_high = high;
		_min = min;
		_max = max;
	}

	public double getHigh() {
		return _high;
	}

	public double getKurtosis() {
		return _kurtosis;
	}

	public double getLow() {
		return _low;
	}

	public double getMax() {
		return _max;
	}

	public double getMean() {
		return _mean;
	}

	public double getMedian() {
		return _median;
	}

	public double getMin() {
		return _min;
	}

	public double getSkewness() {
		return _skewness;
	}

	public double getVariance() {
		return _variance;
	}

	public static SampleSummaryPoint of(
		final double mean,
		final double variance,
		final double skewness,
		final double kurtosis,
		final double median,
		final double low,
		final double high,
		final double min,
		final double max
	) {
		return new SampleSummaryPoint(
			mean,
			variance,
			skewness,
			kurtosis,
			median,
			low,
			high,
			min,
			max
		);
	}

}
