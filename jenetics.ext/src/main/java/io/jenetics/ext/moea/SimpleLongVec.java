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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.moea;

import static io.jenetics.internal.util.SerialIO.readLongArray;
import static io.jenetics.internal.util.SerialIO.writeLongArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
final class SimpleLongVec implements Vec<long[]>, Serializable {
	private static final long serialVersionUID = 2L;

	private final long[] _data;

	SimpleLongVec(final long[] data) {
		Vecs.checkVecLength(data.length);
		_data = data;
	}

	@Override
	public long[] data() {
		return _data;
	}

	@Override
	public int length() {
		return _data.length;
	}

	@Override
	public ElementComparator<long[]> comparator() {
		return SimpleLongVec::cmp;
	}

	private static int cmp(final long[] u, final long[] v, final int i) {
		return Long.compare(u[i], v[i]);
	}

	@Override
	public ElementDistance<long[]> distance() {
		return SimpleLongVec::dist;
	}

	private static double dist(final long[] u, final long[] v, final int i) {
		return u[i] - v[i];
	}

	@Override
	public Comparator<long[]> dominance() {
		return Pareto::dominance;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_data);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof SimpleLongVec &&
			Arrays.equals(((SimpleLongVec) obj)._data, _data);
	}

	@Override
	public String toString() {
		return Arrays.toString(_data);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.SIMPLE_LONG_VEC, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeLongArray(_data, out);
	}

	static SimpleLongVec read(final DataInput in) throws IOException {
		return new SimpleLongVec(readLongArray(in));
	}
}
