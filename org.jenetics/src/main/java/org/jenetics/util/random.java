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
 * @version 1.1 &mdash; <em>$Date: 2012-12-06 $</em>
 */
class random {

	private random() {
		throw new AssertionError("Don't create an 'random' instance.");
	}

	static byte[] seedBytes(final int length) {
		return seed(new byte[length]);
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

	/**
	 * Calculating a 64 bit seed value which can be used for initializing PRNGs.
	 * This method uses a combination of {@code System.nanoTime()} and
	 * {@code new Object().hashCode()} calls to create a reasonable safe seed
	 * value.
	 *
	 * @return the random seed value.
	 */
	static long seed() {
		return seed(nanoTimeSeed());
	}

	/**
	 * Uses the given {@code base} value to create a reasonable safe seed value
	 * by combining it with values of {@code new Object().hashCode()}
	 *
	 * @param base the base value of the seed to create
	 * @return the created seed value.
	 */
	static long seed(final long base) {
		long seed = base ^ objectHashSeed();
		seed ^= seed << 17;
		seed ^= seed >>> 31;
		seed ^= seed << 8;
		return seed;
	}


	private static long objectHashSeed() {
		return ((long)(new Object().hashCode()) << 32) | new Object().hashCode();
	}

	private static long nanoTimeSeed() {
		return
		((System.nanoTime() & 255) << 56) |
		((System.nanoTime() & 255) << 24) |
		((System.nanoTime() & 255) << 48) |
		((System.nanoTime() & 255) << 16) |
		((System.nanoTime() & 255) << 40) |
		((System.nanoTime() & 255) <<  8) |
		((System.nanoTime() & 255) << 32) |
		((System.nanoTime() & 255) <<  0);
	}

}



