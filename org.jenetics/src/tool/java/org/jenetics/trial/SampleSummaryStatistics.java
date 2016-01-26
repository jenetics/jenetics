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

import static java.lang.String.format;

import java.util.function.Consumer;
import java.util.stream.Collector;

import org.jenetics.internal.util.require;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class SampleSummaryStatistics implements Consumer<Sample> {

	private final int _size;
	private final ISeq<DoubleMomentStatistics> _moments;
	private final ISeq<ExactQuantile> _quantiles;

	public SampleSummaryStatistics(final int size) {
		_size = require.positive(size);
		_moments = MSeq.of(DoubleMomentStatistics::new, size).toISeq();
		_quantiles = MSeq.of(ExactQuantile::new, size).toISeq();
	}

	@Override
	public void accept(final Sample sample) {
		if (sample.size() != _size) {
			throw new IllegalArgumentException(format(
				"Expected sample size of %d, but got %d.",
				_moments.size(), sample.size()
			));
		}

		for (int i = 0; i < _size; ++i) {
			_moments.get(i).accept(sample.get(i));
			_quantiles.get(i).accept(sample.get(i));
		}
	}

	public SampleSummaryStatistics combine(final SampleSummaryStatistics statistics) {
		if (statistics._size != _size) {
			throw new IllegalArgumentException(format(
				"Expected sample size of %d, but got %d.",
				_size, statistics._size
			));
		}

		for (int i = 0; i < _size; ++i) {
			_moments.get(i).combine(statistics._moments.get(i));
			_quantiles.get(i).combine(statistics._quantiles.get(i));
		}

		return this;
	}

	public ISeq<DoubleMomentStatistics> getMoments() {
		return _moments;
	}

	public ISeq<ExactQuantile> getQuantiles() {
		return _quantiles;
	}

	public static Collector<Sample, ?, SampleSummaryStatistics>
	toSampleStatistics(final int size) {
		require.positive(size);

		return Collector.of(
			() -> new SampleSummaryStatistics(size),
			SampleSummaryStatistics::accept,
			SampleSummaryStatistics::combine
		);
	}

}
