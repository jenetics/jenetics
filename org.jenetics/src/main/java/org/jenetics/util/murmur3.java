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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

/**
 * Murmur3 hashing.
 *
 * @see https://code.google.com/p/smhasher/source/browse/trunk/MurmurHash3.cpp
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date$</em>
 */
final class murmur3 extends StaticObject {
	private murmur3() {}

	/**
	 * Hashing two long keys together with a given seed.
	 */
	static long hash64(final long key1, final long key2, final long seed) {
		long h1 = 0x9368e53c2f6af274L ^ seed;
		long h2 = 0x586dcd208f7cd3fdL ^ seed;

		long c1 = 0x87c37b91114253d5L;
		long c2 = 0x4cf5ad432745937fL;

		long k1 = key1;
		long k2 = key2;

		// bmix ////////////////////////////////////////////////////////////////
		k1 *= c1;
		k1 = (k1 << 23) | (k1 >>> 64 - 23);
		k1 *= c2;
		h1 ^= k1;
		h1 += h2;

		h2 = (h2 << 41) | (h2 >>> 64 - 41);

		k2 *= c2;
		k2 = (k2 << 23) | (k2 >>> 64 - 23);
		k2 *= c1;
		h2 ^= k2;
		h2 += h1;

		h1 = h1*3 + 0x52dce729;
		h2 = h2*3 + 0x38495ab5;

		c1 = c1*5 + 0x7b7d159c;
		c2 = c2*5 + 0x6bce6396;
		////////////////////////////////////////////////////////////////////////

		h2 ^= 16;

		h1 += h2;
		h2 += h1;

		h1 = fmix(h1);
		h2 = fmix(h2);

		h1 += h2;
		h2 += h1;

		return h1;
	}

	private static long fmix(long key) {
		long k = key ^ (key >>> 33);
		k *= 0xff51afd7ed558ccdL;
		k ^= k >>> 33;
		k *= 0xc4ceb9fe1a85ec53L;
		k ^= k >>> 33;

		return k;
	}

}
