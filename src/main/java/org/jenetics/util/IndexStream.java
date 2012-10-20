/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static java.lang.String.format;

import java.util.Random;

/**
 * Interface which delivers a stream of (positive) indexes ({@code int}s)s. The
 * stream ends if {@link #next()} returns {@code -1}. Here some usage examples:
 *
 * [code]
 * final IndexStream stream = ...;
 * for (int i = stream.next(); i != -1; i = stream.next()) {
 *     System.out.println(i);
 * }
 * [/code]
 * [code]
 * final IndexStream stream = ...;
 * int index = 0;
 * while ((index = stream.next()) != -1) {
 *     System.out.println(index);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0
 */
public abstract class IndexStream {

	protected IndexStream() {
	}

	/**
	 * Return the next (positive inclusive zero) index, or -1 if the stream has
	 * reached its end.
	 *
	 * @return the next index, or -1 if the stream has reached its end.
	 */
	public abstract int next();

	/**
	 * Create a new random IndexIterator.
	 * @param n the maximal value (exclusively) the created index stream will
	 *         return.
	 * @param probability the index selection probability.
	 * @param random the random engine used for creating the random indexes.
	 * @throws IllegalArgumentException if {@code n == Integer.MAX_VALUE} or
	 *         {@code n <= 0} or the given {@code probability} is not valid.
	 * @throws NullPointerException if the given {@code random} engine is
	 *         {@code null}.
	 */
	public static IndexStream Random(
		final int n,
		final double probability,
		final Random random
	) {
		if (n == Integer.MAX_VALUE) {
			throw new IllegalArgumentException(format(
				"n must be smaller than Integer.MAX_VALUE."
			));
		}
		if (n <= 0) {
			throw new IllegalArgumentException(format(
				"n must be greate than zero: %d", n
			));
		}

		return new IndexStream() {
			private int _pos = -1;

			@Override
			public int next() {
				while (_pos < n && random.nextDouble() >= probability) {
					++_pos;
				}
				if (_pos < n) {
					++_pos;
				}

				return _pos < n ? _pos : -1;
			}

		};
	}

}


