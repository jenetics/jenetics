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

import static java.util.Objects.requireNonNull;

import java.util.stream.Collector;
import java.util.stream.IntStream;

import org.jenetics.internal.util.require;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__! &mdash; <em>$Date$</em>
 * @since !__version__!
 */
public class SampleSummary {

	private final ISeq<SampleSummaryPoint> _points;

	private SampleSummary(final ISeq<SampleSummaryPoint> points) {
		_points = requireNonNull(points);
	}

	public ISeq<SampleSummaryPoint> getPoints() {
		return _points;
	}

	public static SampleSummary of(final ISeq<SampleSummaryPoint> points) {
		return new SampleSummary(points);
	}

	public static SampleSummary of(final SampleStatistics statistics) {
		final ISeq<DoubleMomentStatistics> moments = statistics.getMoments();
		final ISeq<ExactQuantile> quantiles = statistics.getQuantiles();

		return of(
			IntStream.range(0, moments.size())
				.mapToObj(i -> toPoint(moments.get(i), quantiles.get(i)))
				.collect(ISeq.toISeq())
		);
	}

	private static SampleSummaryPoint toPoint(
		final DoubleMomentStatistics moment,
		final ExactQuantile quantile
	) {
		return SampleSummaryPoint.of(
			moment.getMean(),
			moment.getVariance(),
			moment.getSkewness(),
			moment.getKurtosis(),
			quantile.quantile(0.5),
			quantile.quantile(0.25),
			quantile.quantile(0.75),
			moment.getMin(),
			moment.getMax()
		);
	}

	public static Collector<Sample, ?, SampleSummary>
	toSampleSummary(final int size) {
		require.positive(size);

		return Collector.of(
			() -> new SampleStatistics(size),
			SampleStatistics::accept,
			SampleStatistics::combine,
			SampleSummary::of
		);
	}

}
