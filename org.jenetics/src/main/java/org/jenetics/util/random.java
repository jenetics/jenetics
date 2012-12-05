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


/**
 * Some helper method concerning random number generation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
class random {

	private random() {
		throw new AssertionError("Don't create an 'random' instance.");
	}

	static byte[] seed(final byte[] seed) {
		for (int i = 0, len = seed.length; i < len;) {
			int n = Math.min(len - i, Long.SIZE/Byte.SIZE);

			for (long x = seed(); n-- > 0; x >>= Byte.SIZE) {
				seed[i++] = (byte)x;
			}
		}

		return seed;
	}

	static long seed(final long seed) {
		long s = seed^nanoTimeSeed();
		s ^= s << 21;
		s ^= s >>> 35;
		s ^= s << 4;
		return s;
	}

	/**
	 * Calculating a 64 bit seed value which can be used for initializing PRNGs.
	 *
	 * @return the random seed value.
	 */
	static long seed() {
		long seed = nanoTimeSeed();
		seed ^= seed(seed);
		seed ^= seed(seed);
		seed ^= seed(seed);
		return seed;
	}

	private static long nanoTimeSeed() {
		return
		((System.nanoTime() & 255) << 56) |
		((System.nanoTime() & 255) <<  0) |
		((System.nanoTime() & 255) << 32) |
		((System.nanoTime() & 255) << 16) |
		((System.nanoTime() & 255) << 24) |
		((System.nanoTime() & 255) << 40) |
		((System.nanoTime() & 255) <<  8) |
		((System.nanoTime() & 255) << 48);
	}

}



