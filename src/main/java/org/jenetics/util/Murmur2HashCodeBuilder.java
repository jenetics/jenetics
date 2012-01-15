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
 * This is an implementation by Viliam Holub of the fast non-cryptographic 
 * murmurhash2 algorithm. 
 *  
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class Murmur2HashCodeBuilder implements HashCodeBuilder {

	private int _hash = 0;
	
	private byte[] _temp = new byte[8];
	
	// https://github.com/tnm/murmurhash-java/blob/master/src/main/java/ie/ucd/murmur/MurmurHash.java
	static int hash32(final byte[] data, final int length, final int seed) {
		// 'm' and 'r' are mixing constants generated offline.
		// They're not really 'magic', they just happen to work well.
		final int m = 0x5bd1e995;
		final int r = 24;
		
		// Initialize the hash to a random value
		int h = seed^length;
		final int length4 = length/4;

		for (int i = 0; i < length4; i++) {
			final int i4 = i*4;
			int k = (data[i4 + 0]&0xff) + 
					((data[i4 + 1]&0xff) << 8) + 
					((data[i4 + 2]&0xff) << 16) +
					((data[i4 + 3]&0xff) << 24);
			
			k *= m;
			k ^= k >>> r;
			k *= m;
			h *= m;
			h ^= k;
		}

		// Handle the last few bytes of the input array
		switch (length%4) {
			case 3: h ^= (data[(length&~3) +2]&0xff) << 16;
			case 2: h ^= (data[(length&~3) +1]&0xff) << 8;
			case 1: h ^= (data[length&~3]&0xff);
			h *= m;
		}

		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;

		return h;
	}
	
	@Override
	public HashCodeBuilder and(boolean value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(boolean[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(final byte value) {
		_temp[0] = value;
		_hash = hash32(_temp, 1, _hash);
		return this;
	}

	@Override
	public HashCodeBuilder and(final byte[] values) {
		_hash = hash32(values, values.length, _hash);
		return this;
	}

	@Override
	public HashCodeBuilder and(char value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(char[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(short value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(short[] values) {
		return null;
	}

	@Override
	public HashCodeBuilder and(int value) {
		return null;
	}

	@Override
	public HashCodeBuilder and(int[] values) {
		return null;
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

}
