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
 * @since 1.0
 * @version $Id$
 */
final class MurmurHashCodeBuilder extends HashCodeBuilder {

	MurmurHashCodeBuilder(final Class<?> type) {
		super(type);
	}


	@Override
	public HashCodeBuilder and(final boolean value) {
		_hash = mix(_hash, value ? 1 : 0);
		return this;
	}

	@Override
	public HashCodeBuilder and(final boolean[] values) {
		for (int i = 0; i < values.length; ++i) {
			_hash = mix(_hash, values[i] ? 1 : 0);
		}

		_hash = finalizeHash(_hash, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(final byte value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(final byte[] values) {
		_hash = bytesHash(values, _hash);
		return this;
	}

	@Override
	public HashCodeBuilder and(final char value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(final char[] values) {
		for (int i = 0; i < values.length; ++i) {
			_hash = mix(_hash, values[i]);
		}

		_hash = finalizeHash(_hash, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(final short value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(final short[] values) {
		for (int i = 0; i < values.length; ++i) {
			_hash = mix(_hash, values[i]);
		}

		_hash = finalizeHash(_hash, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(int value) {
		_hash = mix(_hash, value);
		return this;
	}

	@Override
	public HashCodeBuilder and(final int[] values) {
		for (int i = 0; i < values.length; ++i) {
			_hash = mix(_hash, values[i]);
		}
		_hash = finalizeHash(_hash, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(final long value) {
		_hash = mix(_hash, (int)((value >>> 32) & 0xFFFFFFFF));
		_hash = mix(_hash, (int)((value >>>  0) & 0xFFFFFFFF));
		_hash = finalizeHash(_hash, 2);
		return this;
	}

	@Override
	public HashCodeBuilder and(final long[] values) {
		for (int i = 0; i < values.length; ++i) {
			_hash = mix(_hash, (int)((values[i] >>> 32) & 0xFFFFFFFF));
			_hash = mix(_hash, (int)((values[i] >>>  0) & 0xFFFFFFFF));
		}
		_hash = finalizeHash(_hash, values.length*2);
		return this;
	}

	@Override
	public HashCodeBuilder and(float value) {
		and(Float.floatToIntBits(value));
		return this;
	}

	@Override
	public HashCodeBuilder and(final float[] values) {
		for (int i = 0; i < values.length; ++i) {
			_hash = mix(_hash, Float.floatToIntBits(values[i]));
		}
		_hash = finalizeHash(_hash, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(final double value) {
		and(Double.doubleToLongBits(value));
		return this;
	}

	@Override
	public HashCodeBuilder and(final double[] values) {
		for (int i = 0; i < values.length; ++i) {
			final long value = Double.doubleToLongBits(values[i]);
			_hash = mix(_hash, (int)((value >>> 32) & 0xFFFFFFFF));
			_hash = mix(_hash, (int)((value >>>  0) & 0xFFFFFFFF));
		}
		_hash = finalizeHash(_hash, values.length*2);
		return this;
	}

	private void and(final String value) {
		for (int i = 0; i < value.length(); ++i) {
			_hash = mix(_hash, value.charAt(i));
		}
		_hash = finalizeHash(_hash, value.length());
	}

	@Override
	public HashCodeBuilder and(final Object value) {
		if (value instanceof String) {
			and((String)value);
		} else {
			and(value.hashCode());
		}
		return this;
	}

	@Override
	public HashCodeBuilder and(final Object[] values) {
		for (int i = 0; i < values.length; ++i) {
			if (values[i] instanceof String) {
				and((String)values[i]);
			} else {
				_hash = mix(_hash, values[i].hashCode());
			}
		}
		_hash = finalizeHash(_hash, values.length);
		return this;
	}

	@Override
	public HashCodeBuilder and(final Seq<?> values) {
		for (int i = 0; i < values.length(); ++i) {
			if (values.get(i) instanceof String) {
				and((String)values.get(i));
			} else {
				_hash = mix(_hash, values.get(i).hashCode());
			}
		}
		_hash = finalizeHash(_hash, values.length());
		return this;
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

	private static int mixLast(final int hash, final int data) {
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
