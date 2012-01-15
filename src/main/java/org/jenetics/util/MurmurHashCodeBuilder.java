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
 * Scala mumur implementation.
 *  
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class MurmurHashCodeBuilder extends HashCodeBuilder {

	MurmurHashCodeBuilder(final Class<?> type) {
		super(type);
	}
	
	
	@Override
	public HashCodeBuilder and(boolean value) {
		_hash = mix(_hash, value ? 1 : 0);
		return this;
	}

	@Override
	public HashCodeBuilder and(boolean[] values) {
		int h = _hash;
		for (int i = 0; i < values.length; ++i) {
			h = mix(h, values[i] ? 1 : 0);
		}
		
		_hash = finalizeHash(h, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(final byte value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(final byte[] values) {
		int h = _hash;
		for (int i = 0; i < values.length; ++i) {
			h = mix(h, values[i]);
		}
		
		_hash = finalizeHash(h, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(char value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(char[] values) {
		int h = _hash;
		for (int i = 0; i < values.length; ++i) {
			h = mix(h, values[i]);
		}
		
		_hash = finalizeHash(h, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(short value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(short[] values) {
		int h = _hash;
		for (int i = 0; i < values.length; ++i) {
			h = mix(h, values[i]);
		}
		
		_hash = finalizeHash(h, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(int value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(int[] values) {
		int h = _hash;
		for (int i = 0; i < values.length; ++i) {
			h = mix(h, values[i]);
		}
		_hash = finalizeHash(h, values.length);
		
		return this;
	}

	@Override
	public HashCodeBuilder and(long value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(long[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(float value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(float[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(double value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(double[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(Object value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(Object[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(Seq<?> values) {
		return null;
	}

	@Override
	public int value() {
		return _hash;
	}

	/*
	 * Static hashing methods.
	 */
	
	private static int bytesHash(final byte[] data, final int seed) {
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
	
	private static int mix(final int hash, final int data) {
		int h = mixLast(hash, data);
		h = Integer.rotateLeft(h, 13);
		return h*5 + 0xe6546b64;
	}
	
	private static int mixLast(final int data, final int hash) {
		int k = data;

		k *= 0xcc9e2d51;
		k = Integer.rotateLeft(k, 15);
		k *= 0x1b873593;

		return hash^k;
	}
	
	private static int finalizeHash(final int hash, final int length) {
		return avalanche(hash^length);
	}

	private static final int avalanche(int hash) {
		int h = hash;

		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;

		return h;
	}
	
}
