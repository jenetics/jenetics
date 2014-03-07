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
package org.jenetics.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.StreamSupport;

import org.jenetics.internal.math.probability;

/**
 * Interface which delivers a stream of (positive) indexes ({@code int}s)s. The
 * stream ends if {@link #next()} returns {@code -1}. Here some usage examples:
 *
 * [code]
 * final IndexStream stream = ...;
 * for (int index = stream.next(); index != -1; index = stream.next()) {
 *     System.out.println(index);
 * }
 * [/code]
 * [code]
 * final IndexStream stream = ...;
 * int index = 0;
 * while ((index = stream.next()) != -1) {
 *     System.out.println(index);
 * }
 * [/code]
 * [code]
 * IndexStream.Random(1000, 0.6).forEach(index -> {
 *     System.out.println(index);
 * });
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version @__version__@ &mdash; <em>$Date: 2014-03-07 $</em>
 */
public abstract class IndexStream extends IntStreamAdapter {

	protected IndexStream() {
		setAdoptee(StreamSupport.intStream(
			() -> new IndexSpliterator(this),
			IndexSpliterator.CHARACTERISTICS,
			false
		));
	}

	/**
	 * Return the next (positive inclusive zero) index, or -1 if the stream has
	 * reached its end.
	 *
	 * @return the next index, or -1 if the stream has reached its end.
	 */
	public abstract int next();

	/**
	 * Applies a {@code function} to all elements of this stream.
	 *
	 * @param consumer the function to apply to the elements.
	 * @throws NullPointerException if the given {@code function} is
	 *         {@code null}.
	 */
	@Override
	public void forEach(final IntConsumer consumer) {
		for (int i = next(); i != -1; i = next()) {
			consumer.accept(i);
		}
	}

	/**
	 * Create a new random IndexIterator.
	 * @param n the maximal value (exclusively) the created index stream will
	 *         return.
	 * @param probability the index selection probability.
	 * @throws IllegalArgumentException if {@code n == Integer.MAX_VALUE} or
	 *         {@code n <= 0} or the given {@code probability} is not valid.
	 */
	public static IndexStream Random(final int n, final double probability) {
		return Random(n, probability, RandomRegistry.getRandom());
	}

	/**
	 * Create a new random IndexIterator. The elements returned by this stream
	 * are strictly increasing.
	 *
	 * @param n the maximal value (exclusively) the created index stream will
	 *        return.
	 * @param p the index selection probability.
	 * @param random the random engine used for creating the random indexes.
	 * @throws IllegalArgumentException if {@code n == Integer.MAX_VALUE} or
	 *         {@code n <= 0} or the given {@code probability} is not valid.
	 * @throws NullPointerException if the given {@code random} engine is
	 *         {@code null}.
	 */
	public static IndexStream Random(
		final int n,
		final double p,
		final Random random
	) {
		if (n == Integer.MAX_VALUE) {
			throw new IllegalArgumentException(format(
				"n must be smaller than Integer.MAX_VALUE."
			));
		}
		if (n <= 0) {
			throw new IllegalArgumentException(format(
				"n must be greater than zero: %d", n
			));
		}

		IndexStream stream = new RandomIndexStream(n, p, random);
		if (Double.compare(p, 0.0) == 0) {
			stream = new RandomIndexStreamP0();
		} else if (Double.compare(p, 1.0) == 0) {
			stream = new RandomIndexStreamP1(n);
		}
		return stream;
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since @__version__@
	 * @version @__version__@ &mdash; <em>$Date: 2014-03-07 $</em>
	 */
	final static class IndexSpliterator implements Spliterator.OfInt {
		private final IndexStream _stream;

		final static int CHARACTERISTICS = Spliterator.IMMUTABLE;

		IndexSpliterator(final IndexStream stream) {
			_stream = stream;
		}

		@Override
		public OfInt trySplit() {
			return null;
		}

		@Override
		public boolean tryAdvance(final IntConsumer action) {
			final int index = _stream.next();
			if (index != -1) {
				action.accept(index);
				return true;
			}
			return false;
		}

		@Override
		public long estimateSize() {
			return Long.MAX_VALUE;
		}

		@Override
		public int characteristics() {
			return CHARACTERISTICS;
		}
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.4
	 * @version @__version__@ &mdash; <em>$Date: 2014-03-07 $</em>
	 */
	final static class RandomIndexStream extends IndexStream {
		private final int _n;
		private final int _p;
		private final Random _random;

		private int _pos = -1;

		RandomIndexStream(final int n, final double p, final Random random) {
			_n = n;
			_p = probability.toInt(p);
			_random = requireNonNull(random, "Random object must not be null.");
		}

		@Override
		public final int next() {
			while (_pos < _n && _random.nextInt() >= _p) {
				++_pos;
			}
			++_pos;

			return _pos < _n ? _pos : -1;
		}

		@Override
		public void forEachOrdered(final IntConsumer consumer) {
			for (int i = next(); i != -1; i = next()) {
				consumer.accept(i);
			}
		}
	}
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-03-07 $</em>
 */
final class RandomIndexStreamP0 extends IndexStream {
	@Override public int next() {
		return -1;
	}
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-03-07 $</em>
 */
final class RandomIndexStreamP1 extends IndexStream {
	private final int _n;
	private int _pos = -1;

	RandomIndexStreamP1(final int n) {
		_n = n;
	}

	@Override public int next() {
		++_pos;
		return _pos < _n ? _pos : -1;
	}
}
