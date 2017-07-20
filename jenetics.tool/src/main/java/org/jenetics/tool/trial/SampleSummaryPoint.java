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
package org.jenetics.tool.trial;

import static java.lang.Double.compare;
import static java.lang.String.format;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a point of the sample summary. The class contains the following
 * values:
 * <ul>
 *     <li>mean</li>
 *     <li>variance</li>
 *     <li>skewness</li>
 *     <li>kurtosis</li>
 *     <li>median</li>
 *     <li>low (<i>25-percentile</i>)</li>
 *     <li>high (<i>75-percentile</i>)</li>
 *     <li>min</li>
 *     <li>max</li>
 * </ul>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public final class SampleSummaryPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant with all values set to {@link Double#NaN}.
	 */
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

	/**
	 * Return the 75-percentile
	 *
	 * @return the 75-percentile
	 */
	public double getHigh() {
		return _high;
	}

	/**
	 * The kurtosis value.
	 *
	 * @return the kurtosis value
	 */
	public double getKurtosis() {
		return _kurtosis;
	}

	/**
	 * The 25-percentile
	 *
	 * @return the 25-percentile
	 */
	public double getLow() {
		return _low;
	}

	/**
	 * The maximum value.
	 *
	 * @return the maximum value
	 */
	public double getMax() {
		return _max;
	}

	/**
	 * The mean value.
	 *
	 * @return the mean value
	 */
	public double getMean() {
		return _mean;
	}

	/**
	 * The median value.
	 *
	 * @return the median value
	 */
	public double getMedian() {
		return _median;
	}

	/**
	 * The minimum value.
	 *
	 * @return the minimum value
	 */
	public double getMin() {
		return _min;
	}

	/**
	 * The skewness value.
	 *
	 * @return the skewness value
	 */
	public double getSkewness() {
		return _skewness;
	}

	/**
	 * The variance value.
	 *
	 * @return the variance value
	 */
	public double getVariance() {
		return _variance;
	}

	/**
	 * Return the double values of this point as array in the following order:
	 * <ul>
	 *     <li>mean</li>
	 *     <li>variance</li>
	 *     <li>skewness</li>
	 *     <li>kurtosis</li>
	 *     <li>median</li>
	 *     <li>low (<i>25-percentile</i>)</li>
	 *     <li>high (<i>75-percentile</i>)</li>
	 *     <li>min</li>
	 *     <li>max</li>
	 * </ul>
	 *
	 * @return the double values of this point as array
	 */
	public double[] toArray() {
		return new double[] {
			_mean, _variance, _skewness, _kurtosis,
			_median, _low, _high, _min, _max
		};
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(toArray());
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof SampleSummaryPoint &&
			compare(_mean, ((SampleSummaryPoint)obj)._mean) == 0 &&
			compare(_variance, ((SampleSummaryPoint)obj)._variance) == 0 &&
			compare(_skewness, ((SampleSummaryPoint)obj)._skewness) == 0 &&
			compare(_kurtosis, ((SampleSummaryPoint)obj)._kurtosis) == 0 &&
			compare(_median, ((SampleSummaryPoint)obj)._median) == 0 &&
			compare(_low, ((SampleSummaryPoint)obj)._low) == 0 &&
			compare(_high, ((SampleSummaryPoint)obj)._high) == 0 &&
			compare(_min, ((SampleSummaryPoint)obj)._min) == 0 &&
			compare(_max, ((SampleSummaryPoint)obj)._max) == 0;
	}

	@Override
	public String toString() {
		return format(
			"Point[mean=%f, variance=%f, skewness=%f, kurtosis=%f, " +
			"median=%f, low=%f, high=%f, min=%f, max=%f]",
			_mean,
			_variance,
			_skewness,
			_kurtosis,
			_median,
			_low,
			_high,
			_min,
			_max
		);
	}

	/**
	 * Create a new {@code SampleSummaryPoint} from the given values.
	 *
	 * @param mean the mean value
	 * @param variance the variance value
	 * @param skewness the skewness value
	 * @param kurtosis the kurtosis value
	 * @param median the median value
	 * @param low the 25-percentile
	 * @param high the 75-percentile
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return a new {@code SampleSummaryPoint} from the given values
	 */
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
