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
package org.jenetics.engine;

import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

import java.util.function.BiPredicate;
import java.util.function.DoubleConsumer;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.DoubleMoments;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class FitnessConvergenceLimit<N extends Number & Comparable<? super N>>
	implements Predicate<EvolutionResult<?, N>>
{

	private final Buffer _shortBuffer;
	private final Buffer _longBuffer;
	private final BiPredicate<DoubleMoments, DoubleMoments> _limit;

	private long _generation;

	FitnessConvergenceLimit(
		final int shortFilterSize,
		final int longFilterSize,
		final BiPredicate<DoubleMoments, DoubleMoments> limit
	) {
		_shortBuffer = new Buffer(shortFilterSize);
		_longBuffer = new Buffer(longFilterSize);
		_limit = requireNonNull(limit);
	}

	@Override
	public boolean test(final EvolutionResult<?, N> result) {
		final Number fitness = result.getBestFitness();

		if (fitness != null) {
			_shortBuffer.accept(fitness.doubleValue());
			_longBuffer.accept(fitness.doubleValue());
			++_generation;
		}

		return _generation < _longBuffer.capacity() ||
			_limit.test(_shortBuffer.doubleMoments(), _longBuffer.doubleMoments());
	}

	/**
	 *
	 */
	static final class Buffer implements DoubleConsumer {
		private final double[] _buffer;

		private int _pos;
		private int _length;
		private long _samples;


		Buffer(final int length) {
			_buffer = new double[length];
		}

		@Override
		public void accept(final double value) {
			_buffer[_pos] = value;

			_pos = (_pos + 1)%_buffer.length;
			_length = min(_length + 1, _buffer.length);
			++_samples;
		}

		public int capacity() {
			return _buffer.length;
		}

		public int length() {
			return _length;
		}

		public long samples() {
			return _samples;
		}

		public DoubleStream stream(final int windowSize) {
			final int length = min(windowSize, _length);

			return IntStream.range(0, length)
				.map(i -> (_pos + _buffer.length - length + i)%_buffer.length)
				.mapToDouble(i -> _buffer[i]);
		}


		public DoubleMoments doubleMoments(final int windowSize) {
			return DoubleMoments.of(
				stream(windowSize).collect(
					DoubleMomentStatistics::new,
					DoubleMomentStatistics::accept,
					DoubleMomentStatistics::combine
				)
			);

			/*
			final int length = min(windowSize, _length);
			final DoubleMomentStatistics statistics = new DoubleMomentStatistics();

			for (int i = length; --i >= 0;) {
				final int index = (_pos - 1 + _buffer.length - i)%_buffer.length;
				statistics.accept(_buffer[index]);
			}

			return DoubleMoments.of(statistics);
			*/
		}

		public DoubleMoments doubleMoments() {
			final DoubleMomentStatistics statistics = new DoubleMomentStatistics();
			for (int i = _buffer.length; --i >=0;) {
				statistics.accept(_buffer[i]);
			}

			return DoubleMoments.of(statistics);
		}
	}

}
