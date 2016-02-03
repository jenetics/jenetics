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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.internal.util.require;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.ISeq;

/**
 * Summary of a given set of {@link Sample} objects.
 *
 * @see Data
 * @see Sample
 * @see SampleSummaryPoint
 * @see SampleSummaryStatistics
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public final class SampleSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ISeq<SampleSummaryPoint> _points;

	private SampleSummary(final ISeq<SampleSummaryPoint> points) {
		_points = requireNonNull(points);
	}

	/**
	 * Return the number of {@link SampleSummaryPoint} this summary, one for
	 * every parameter.
	 *
	 * @return the number of parameters
	 */
	public int parameterCount() {
		return _points.size();
	}

	/**
	 * Return the summary points.
	 *
	 * @return the summary points
	 */
	public ISeq<SampleSummaryPoint> getPoints() {
		return _points;
	}

	/**
	 * Return the summary points as {@link Stream}.
	 *
	 * @return the summary points as {@link Stream}
	 */
	public Stream<SampleSummaryPoint> stream() {
		return _points.stream();
	}

	@Override
	public int hashCode() {
		return _points.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof SampleSummary &&
			_points.equals(((SampleSummary)obj)._points);
	}

	/**
	 * Create a new {@code SampleSummary} object from the given sample points.
	 *
	 * @param points the summary points
	 * @return a new {@code SampleSummary} instance
	 * @throws NullPointerException if the argument is {@code null}
	 */
	public static SampleSummary of(final ISeq<SampleSummaryPoint> points) {
		return new SampleSummary(points);
	}

	/**
	 * Return a new {@code SampleSummary} for the given
	 * {@link SampleSummaryStatistics}.
	 *
	 * @param statistics the summary statistics object
	 * @return a new {@code SampleSummary} instance
	 * @throws NullPointerException if the argument is {@code null}
	 */
	public static SampleSummary of(final SampleSummaryStatistics statistics) {
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

	/**
	 * Return a {@link Collector} for creating a {@code SampleSummary} object.
	 *
	 * @param parameterCount the number of parameters of the samples
	 * @return a {@code SampleSummary} {@link Collector}
	 * @throws IllegalArgumentException if the given {@code parameterCount} is
	 *         smaller than one
	 */
	public static Collector<Sample, ?, SampleSummary>
	toSampleSummary(final int parameterCount) {
		require.positive(parameterCount);

		return Collector.of(
			() -> new SampleSummaryStatistics(parameterCount),
			SampleSummaryStatistics::accept,
			SampleSummaryStatistics::combine,
			SampleSummary::of
		);
	}

}
