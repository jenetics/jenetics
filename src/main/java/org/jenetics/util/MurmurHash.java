/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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
 * Implementation based on the MurmurHash 3.0 algorithm implementation of 
 * Scala 2.9:
 * http://www.scala-lang.org/api/current/index.html#scala.util.MurmurHash 
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class MurmurHash {

	/** 
	 * Mix in a block of data into an intermediate hash value. 
	 */
	final int mix(int hash, int data) {
		int h = mixLast(hash, data);
		h = Integer.rotateLeft(h, 13);
		return h*5 + 0xe6546b64;
	}

	/**
	 * May optionally be used as the last mixing step. Is a little bit faster
	 * than mix, as it does no further mixing of the resulting hash. For the
	 * last element this is not necessary as the hash is thoroughly mixed during
	 * finalization anyway.
	 */
	final int mixLast(int hash, int data) {
		int k = data;

		k *= 0xcc9e2d51;
		k = Integer.rotateLeft(k, 15);
		k *= 0x1b873593;

		return hash ^ k;
	}

	/**
	 * Finalize a hash to incorporate the length and make sure all bits
	 * avalanche.
	 */
	final int finalizeHash(int hash, int length) {
		return avalanche(hash ^ length);
	}

	/** 
	 * Force all bits of the hash to avalanche. Used for finalizing the hash. 
	 */
	private final int avalanche(int hash) {
		int h = hash;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;

		return h;
	}

	/** 
	 * Compute the hash of a string 
	 */
	final int stringHash(String str, int seed) {
		int h = seed;
		int i = 0;
		while (i + 1 < str.length()) {
			int data = (str.charAt(i) << 16) + str.charAt(i + 1);
			h = mix(h, data);
			i += 2;
		}
		if (i < str.length())
			h = mixLast(h, str.charAt(i));
		return finalizeHash(h, str.length());
	}

	/**
	 * Compute the hash of an array.
	 */
	final <T> int arrayHash(final T[] a, int seed) {
		int h = seed;
		int i = 0;
		while (i < a.length) {
			h = mix(h, a[i].hashCode());
			i += 1;
		}
		return finalizeHash(h, a.length);
	}

	/**
	 * Compute the hash of a byte array. Faster than arrayHash, because it
	 * hashes 4 bytes at once.
	 */
	final int bytesHash(byte[] data, int seed) {
		int len = data.length;
		int h = seed;

		// Body
		int i = 0;
		while (len >= 4) {
			int k = data[i + 0] & 0xFF;
			k |= (data[i + 1] & 0xFF) << 8;
			k |= (data[i + 2] & 0xFF) << 16;
			k |= (data[i + 3] & 0xFF) << 24;

			h = mix(h, k);

			i += 4;
			len -= 4;
		}

		// Tail
		int k = 0;
		if (len == 3)
			k ^= (data[i + 2] & 0xFF) << 16;
		if (len >= 2)
			k ^= (data[i + 1] & 0xFF) << 8;
		if (len >= 1) {
			k ^= (data[i + 0] & 0xFF);
			h = mixLast(h, k);
		}

		// Finalization
		return finalizeHash(h, data.length);
	}

}
