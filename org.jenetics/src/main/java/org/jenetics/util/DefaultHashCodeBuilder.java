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

import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0
 */
final class DefaultHashCodeBuilder extends HashCodeBuilder {
	private static final int P1 = 47;
	private static final int P2 = 103;
	private static final int P3 = 1231;
	private static final int P4 = 1237;


	public DefaultHashCodeBuilder(final Class<?> type) {
		super(type);
	}

	@Override
	public HashCodeBuilder and(final boolean value) {
		_hash += value ? P3 : P4; return this;
	}

	@Override
	public HashCodeBuilder and(final boolean[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final byte value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public HashCodeBuilder and(final byte[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final char value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public HashCodeBuilder and(final char[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final short value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public HashCodeBuilder and(final short[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final int value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public HashCodeBuilder and(final int[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final long value) {
		_hash += P1*(int)(value^(value >>> 32)); return this;
	}

	@Override
	public HashCodeBuilder and(final long[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final float value) {
		_hash += P1*Float.floatToIntBits(value); return this;
	}

	@Override
	public HashCodeBuilder and(final float[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final double value) {
		long bits = Double.doubleToLongBits(value);
		_hash += (int)(bits^(bits >>> 32));
		return this;
	}

	@Override
	public HashCodeBuilder and(final double[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final Object value) {
		_hash += P1*(value == null ? 0 : value.hashCode()) + P2; return this;
	}

	@Override
	public HashCodeBuilder and(final Object[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public HashCodeBuilder and(final Seq<?> values) {
		_hash += arrays.hashCode(values); return this;
	}

}
