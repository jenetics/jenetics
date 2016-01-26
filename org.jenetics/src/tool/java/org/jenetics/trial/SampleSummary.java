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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class SampleSummary {
	public final double mean;
	public final double variance;
	public final double skewness;
	public final double kurtosis;
	public final double median;
	public final double low;
	public final double high;
	public final double min;
	public final double max;

	private SampleSummary(
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
		this.mean = mean;
		this.variance = variance;
		this.skewness = skewness;
		this.kurtosis = kurtosis;
		this.median = median;
		this.low = low;
		this.high = high;
		this.min = min;
		this.max = max;
	}

	public static SampleSummary of(
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
		return new SampleSummary(
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
