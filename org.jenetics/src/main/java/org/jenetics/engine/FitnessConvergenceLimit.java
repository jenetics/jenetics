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
import static java.lang.String.format;
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
	 * Ring buffer for {@code double} values. If the buffer is full, old values
	 * are overridden.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	static final class Buffer implements DoubleConsumer {
		private final double[] _buffer;

		private int _pos;
		private int _length;
		private long _samples;

		/**
		 * Create a new double ring-buffer.
		 *
		 * @param capacity the ring buffer capacity.
		 * @throws IllegalArgumentException if {@code capacity < 1}
		 */
		Buffer(final int capacity) {
			if (capacity < 1) {
				throw new IllegalArgumentException(format(
					"Buffer capacity must be greater than one: %d", capacity
				));
			}

			_buffer = new double[capacity];
		}

		@Override
		public void accept(final double value) {
			_buffer[_pos] = value;

			_pos = (_pos + 1)%_buffer.length;
			_length = min(_length + 1, _buffer.length);
			++_samples;
		}

		/**
		 * Return the capacity of the buffer.
		 *
		 * @return the capacity of the buffer
		 */
		public int capacity() {
			return _buffer.length;
		}

		/**
		 * Return the number of buffer elements.
		 *
		 * @return the number of buffer elements
		 */
		public int length() {
			return _length;
		}

		/**
		 * Return the overall number of elements the buffer has seen.
		 *
		 * @return the overall number of elements the buffer has seen
		 */
		public long samples() {
			return _samples;
		}

		/**
		 * Test if the buffer is full.
		 *
		 * @return {@code true} if the buffer is full, {@code false} otherwise
		 */
		public boolean isFull() {
			return _length == _buffer.length;
		}

		/**
		 * Return a stream of the last ({@code windowSize}) buffer values. The
		 * size if the stream is {@code min(windowSize, length())}.
		 *
		 * @param windowSize the number of stream elements
		 * @return a double value stream
		 */
		public DoubleStream stream(final int windowSize) {
			final int length = min(windowSize, _length);

			return IntStream.range(0, length)
				.map(i -> (_pos + _buffer.length - length + i)%_buffer.length)
				.mapToDouble(i -> _buffer[i]);
		}

		/**
		 * Return the double stream of the buffer values.
		 *
		 * @return the double stream of the buffer values
		 */
		public DoubleStream stream() {
			return stream(_length);
		}

		/**
		 * Return the double moment statistics of the last {@code windowSize}
		 * buffer values.
		 *
		 * @param windowSize the number of the last double values the statistics
		 *        consists of
		 * @return the double moment statistics of the last {@code windowSize}
		 *         buffer values
		 */
		public DoubleMoments doubleMoments(final int windowSize) {
			return DoubleMoments.of(
				stream(windowSize).collect(
					DoubleMomentStatistics::new,
					DoubleMomentStatistics::accept,
					DoubleMomentStatistics::combine
				)
			);
		}

		/**
		 * Return the double moment statistics of the buffer values.
		 *
		 * @return the double moment statistics of the buffer values
		 */
		public DoubleMoments doubleMoments() {
			return doubleMoments(_length);
		}

	}

}
