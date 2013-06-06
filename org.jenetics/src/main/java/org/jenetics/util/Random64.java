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

import java.util.Random;

/**
 * An abstract base class which eases the implementation of {@code Random}
 * objects which natively creates random {@code long} values. All other
 * {@code Random} functions are optimized using this {@code long} values.
 *
 * [code]
 * public class MyRandom64 extends Random64 {
 *     public long nextLong() {
 *         // Only this method must be implemented.
 *         ...
 *     }
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.3 &mdash; <em>$Date$</em>
 */
public abstract class Random64 extends PRNG {

	private static final long serialVersionUID = 1L;

	protected Random64(long seed) {
		super(seed);
	}

	protected Random64() {
		this(math.random.seed());
	}

	/**
	 * Force to explicitly override the Random.nextLong() method. All other
	 * methods of this class are implemented by calling this method.
	 */
	@Override
	public abstract long nextLong();


	@Override
	public boolean nextBoolean() {
		return (nextLong() & 0x8000000000000000L) != 0L;
	}

	@Override
	public int nextInt() {
		return (int)(nextLong() >>> 32);
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}

	/**
	 * Optimized version of the {@link Random#nextBytes(byte[])} method for
	 * 64-bit random engines.
	 */
	@Override
	public void nextBytes(final byte[] bytes) {
		for (int i = 0, len = bytes.length; i < len;) {
			int n = Math.min(len - i, Long.SIZE/Byte.SIZE);

			for (long x = nextLong(); n-- > 0; x >>= Byte.SIZE) {
				bytes[i++] = (byte)x;
			}
		}
	}

	@Override
	public float nextFloat() {
		return math.random.toFloat2(nextLong());
	}

	/**
	 * Optimized version of the {@link Random#nextDouble()} method for 64-bit
	 * random engines.
	 */
	@Override
	public double nextDouble() {
		return math.random.toDouble2(nextLong());
	}

}



