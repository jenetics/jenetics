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
 * Implementation of the fitness-convergence limit strategy object.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.7
 * @since 3.7
 */
final class FitnessConvergenceLimit<N extends Number & Comparable<? super N>>
	implements Predicate<EvolutionResult<?, N>>
{

	private final int _shortFilterSize;
	private final int _longFilterSize;
	private final Buffer _buffer;
	private final BiPredicate<DoubleMoments, DoubleMoments> _proceed;

	/**
	 * Create a new fitness-convergence limit strategy object.
	 *
	 * @param shortFilterSize the size of the short moments filter
	 * @param longFilterSize the size of the long moments filter
	 * @param proceed the predicate which decides whether to stop or proceed the
	 *        evolution stream
	 * @throws IllegalArgumentException if {@code shortFilterSize < 1} or
	 *         {@code longFilterSize < 2} or
	 *         {@code shortFilterSize >= longFilterSize}
	 * @throws NullPointerException if the {@code proceed} predicate is
	 *         {@code null}
	 */
	FitnessConvergenceLimit(
		final int shortFilterSize,
		final int longFilterSize,
		final BiPredicate<DoubleMoments, DoubleMoments> proceed
	) {
		if (shortFilterSize < 1) {
			throw new IllegalArgumentException(format(
				"The short filter size must be greater than one: %d",
				shortFilterSize
			));
		}
		if (longFilterSize < 2) {
			throw new IllegalArgumentException(format(
				"The long filter size must be greater than two: %d",
				shortFilterSize
			));
		}
		if (shortFilterSize >= longFilterSize) {
			throw new IllegalArgumentException(format(
				"The long filter size must be greater than the short " +
				"filter size: %d <= %d",
				longFilterSize, shortFilterSize
			));
		}

		_shortFilterSize = shortFilterSize;
		_longFilterSize = longFilterSize;
		_buffer = new Buffer(longFilterSize);
		_proceed = requireNonNull(proceed);
	}

	@Override
	public boolean test(final EvolutionResult<?, N> result) {
		final Number fitness = result.getBestFitness();

		if (fitness != null) {
			_buffer.accept(fitness.doubleValue());
		}

		return !_buffer.isFull() || _proceed.test(
			_buffer.doubleMoments(_shortFilterSize),
			_buffer.doubleMoments(_longFilterSize)
		);
	}


	/**
	 * Ring buffer for {@code double} values. If the buffer is full, old values
	 * are overridden.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version 3.7
	 * @since 3.7
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
