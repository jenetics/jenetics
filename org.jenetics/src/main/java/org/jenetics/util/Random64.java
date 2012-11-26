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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-11-26 $</em>
 */
abstract class Random64 extends Random {

	private static final long serialVersionUID = 1L;

	protected Random64() {
	}

	protected Random64(long seed) {
		super(seed);
	}

	// Force to explicitly override the Random.nextLong() method.
	@Override
	public abstract long nextLong();

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}


	private boolean _cached = false;
	private int _cache = 0;

	@Override
	public int nextInt() {
		/*
		if (_cached) {
			_cached = false;
			return _cache;
		}

		final long next = nextLong();
		_cache = (int)(next >>> 32);
		_cached = true;
		return (int)next;
		*/
		return (int)nextLong();
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

	/**
	 * Optimized version of the {@link Random#nextDouble()} method for 64-bit
	 * random engines.
	 */
	@Override
	public double nextDouble() {
		return toDouble(nextLong());
	}

	static double toDouble(final long x) {
		return (((x >>> 38) << 27) + (((int)x) >>> 5))/(double)(1L << 53);
	}

}
