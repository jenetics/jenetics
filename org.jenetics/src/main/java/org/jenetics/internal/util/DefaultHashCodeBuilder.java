/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.util.Arrays;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
final class DefaultHashCodeBuilder implements Hash {
	private static final int P1 = 47;
	private static final int P2 = 103;
	private static final int P3 = 1231;
	private static final int P4 = 1237;


	protected int _hash = 0;

	DefaultHashCodeBuilder(final Class<?> type) {
		_hash = type.hashCode();
	}

	@Override
	public DefaultHashCodeBuilder and(final boolean value) {
		_hash += value ? P3 : P4; return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final boolean[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final byte value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final byte[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final char value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final char[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final short value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final short[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final int value) {
		_hash += P1*value + P2; return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final int[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final long value) {
		_hash += P1*Long.hashCode(value); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final long[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final float value) {
		_hash += P1*Float.hashCode(value); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final float[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final double value) {
		_hash += P1*Double.hashCode(value); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final double[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final Object value) {
		_hash += P1*(value == null ? 0 : value.hashCode()) + P2; return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final Object[] values) {
		_hash += Arrays.hashCode(values); return this;
	}

	@Override
	public DefaultHashCodeBuilder and(final Seq<?> values) {
		_hash += Seq.hashCode(values); return this;
	}

	@Override
	public int value() {
		return _hash;
	}
}
