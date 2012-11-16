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
import static org.jenetics.util.object.nonNull;

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
 * @version 1.1 &mdash; <em>$Date: 2012-11-16 $</em>
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
	 * Applies a {@code function} to all elements of this stream.
	 *
	 * @param function the function to apply to the elements.
	 * @throws NullPointerException if the given {@code function} is
	 *          {@code null}.
	 */
	public <R> void foreach(final Function<? super Integer, ? extends R> function) {
		nonNull(function, "Function");
		for (int i = next(); i != -1; i = next()) {
			function.apply(i);
		}
	}

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
			private final long 
			INT_RANGE = (long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE;
			
			private final int 
			MAX = (int)(Math.round(INT_RANGE*probability) + Integer.MIN_VALUE); 
			
			private final int N_MINUS_1 = n - 1;
			
			private int _pos = -1;
			
			private long x = System.nanoTime();
			private int nextInt() {
				x ^= (x << 21);
				x ^= (x >>> 35);
				x ^= (x << 4);
				return (int)(x >>> 32)^(int)(x << 32);
			}
			
			@Override
			public int next() {
				//return (_pos = next(n, _pos, MAX, random));
				
				java.util.concurrent.ThreadLocalRandom r;
				
				while (_pos < n && nextInt() >= MAX) {
					++_pos;
				}
				return (_pos < N_MINUS_1) ? ++_pos : -1;
				
			}

		};
	}
	
	
	private static int next(final int n, final int pos, final int max, Random random) {
		int index = pos;
		while (index < n && random.nextInt() >= max) {
			++index;
		}
		return (index < n - 1) ? (index + 1) : -1;
	}

}


