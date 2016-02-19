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

import static java.lang.String.format;

import java.util.function.Consumer;
import java.util.stream.Collector;

import org.jenetics.internal.util.require;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * A state object for collecting statistics such as count, min, max, sum, mean,
 * variance, skewness and kurtosis. The design of this class is similar to the
 * {@link java.util.DoubleSummaryStatistics} class.
 *
 * <pre>{@code
 * final Stream<Sample> stream = ...
 * final SampleSummaryStatistics statistics = stream.collect(
 *         () -> new SampleSummaryStatistics(parameterCount),
 *         SampleSummaryStatistics::accept,
 *         SampleSummaryStatistics::combine
 *     );
 * }</pre>
 *
 * <p>
 * <b>Implementation note:</b>
 * <i>This implementation is not thread safe. However, it is safe to use
 * {@link #toSampleStatistics(int)}  on a parallel stream,
 * because the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.</i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class SampleSummaryStatistics implements Consumer<Sample> {

	private final int _parameterCount;
	private final ISeq<DoubleMomentStatistics> _moments;
	private final ISeq<ExactQuantile> _quantiles;

	/**
	 * Create a new statistics object with the given expected parameter count of
	 * the accepted samples.
	 *
	 * @param parameterCount the parameter count or sample size, respectively
	 * @throws IllegalArgumentException if the given {@code parameterCount}
	 *         is smaller then one
	 */
	public SampleSummaryStatistics(final int parameterCount) {
		_parameterCount = require.positive(parameterCount);
		_moments = MSeq.of(DoubleMomentStatistics::new, parameterCount).toISeq();
		_quantiles = MSeq.of(ExactQuantile::new, parameterCount).toISeq();
	}

	@Override
	public void accept(final Sample sample) {
		if (sample.size() != _parameterCount) {
			throw new IllegalArgumentException(format(
				"Expected sample size of %d, but got %d.",
				_moments.size(), sample.size()
			));
		}

		for (int i = 0; i < _parameterCount; ++i) {
			_moments.get(i).accept(sample.get(i));
			_quantiles.get(i).accept(sample.get(i));
		}
	}

	/**
	 * Combine two {@code SampleSummaryStatistics} statistic objects.
	 *
	 * @param other the other {@code SampleSummaryStatistics} statistics to
	 *        combine with {@code this} one.
	 * @return {@code this} statistics object
	 * @throws IllegalArgumentException if the {@code parameterCount} of the
	 *         {@code other} statistics object is different to {@code this} one
	 * @throws NullPointerException if the other statistical summary is
	 *         {@code null}.
	 */
	public SampleSummaryStatistics combine(final SampleSummaryStatistics other) {
		if (other._parameterCount != _parameterCount) {
			throw new IllegalArgumentException(format(
				"Expected sample size of %d, but got %d.",
				_parameterCount, other._parameterCount
			));
		}

		for (int i = 0; i < _parameterCount; ++i) {
			_moments.get(i).combine(other._moments.get(i));
			_quantiles.get(i).combine(other._quantiles.get(i));
		}

		return this;
	}

	/**
	 * Return the <i>raw</i> {@code DoubleMomentStatistics} objects.
	 *
	 * @return the <i>raw</i> {@code DoubleMomentStatistics} objects
	 */
	public ISeq<DoubleMomentStatistics> getMoments() {
		return _moments;
	}

	/**
	 * Return the quantile object.
	 *
	 * @return the quantile object
	 */
	public ISeq<ExactQuantile> getQuantiles() {
		return _quantiles;
	}

	/**
	 * Return a {@code Collector} which applies an double-producing mapping
	 * function to each input element, and returns moments-statistics for the
	 * resulting values.
	 *
	 * <pre>{@code
	 * final Stream<Sample> stream = ...
	 * final SampleSummaryStatistics statistics = stream
	 *     .collect(toDoubleMomentStatistics(parameterCount));
	 * }</pre>
	 *
	 * @param parameterCount the number of parameter of the accumulated
	 *        {@code Sample} objects
	 * @return a {@code Collector} implementing the sample reduction
	 * @throws IllegalArgumentException if the given {@code parameterCount}
	 *         is smaller then one
	 */
	public static Collector<Sample, ?, SampleSummaryStatistics>
	toSampleStatistics(final int parameterCount) {
		require.positive(parameterCount);

		return Collector.of(
			() -> new SampleSummaryStatistics(parameterCount),
			SampleSummaryStatistics::accept,
			SampleSummaryStatistics::combine
		);
	}

}
