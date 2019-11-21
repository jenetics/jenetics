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

import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

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
 * @version !__version__!
 * @since !__version__!
 */
final class DoubleVec implements Vec<double[]>, Serializable {
	private static final long serialVersionUID = 1L;
	private final double[] _data;

	DoubleVec(final double[] data) {
		Vecs.checkVecLength(data.length);
		_data = data;
	}

	@Override
	public double[] data() {
		return _data;
	}

	@Override
	public int length() {
		return _data.length;
	}

	@Override
	public ElementComparator<double[]> comparator() {
		return (u, v, i) -> Double.compare(u[i], v[i]);
	}

	@Override
	public ElementDistance<double[]> distance() {
		return (u, v, i) -> u[i] - v[i];
	}

	@Override
	public Comparator<double[]> dominance() {
		return Vec::dominance;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_data);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof DoubleVec &&
				Arrays.equals(((DoubleVec) obj)._data, _data);
	}

	@Override
	public String toString() {
		return Arrays.toString(_data);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.DOUBLE_VEC, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(_data.length, out);
		for (double value : _data) {
			out.writeDouble(value);
		}
	}

	static DoubleVec read(final DataInput in) throws IOException {
		final double[] data = new double[readInt(in)];
		for (int i = 0; i < data.length; ++i) {
			data[i] = in.readDouble();
		}
		return new DoubleVec(data);
	}
}
