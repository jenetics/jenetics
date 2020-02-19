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

import static io.jenetics.internal.util.SerialIO.readDoubleArray;
import static io.jenetics.internal.util.SerialIO.writeDoubleArray;

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
final class SimpleDoubleVec implements Vec<double[]>, Serializable {
	private static final long serialVersionUID = 2L;

	private final double[] _data;

	SimpleDoubleVec(final double[] data) {
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
		return SimpleDoubleVec::cmp;
	}

	private static int cmp(final double[] u, final double[] v, final int i) {
		return Double.compare(u[i], v[i]);
	}

	@Override
	public ElementDistance<double[]> distance() {
		return SimpleDoubleVec::dist;
	}

	private static double dist(final double[] u, final double[] v, final int i) {
		return u[i] - v[i];
	}

	@Override
	public Comparator<double[]> dominance() {
		return Pareto::dominance;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_data);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof SimpleDoubleVec &&
			Arrays.equals(((SimpleDoubleVec)obj)._data, _data);
	}

	@Override
	public String toString() {
		return Arrays.toString(_data);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.SIMPLE_DOUBLE_VEC, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeDoubleArray(_data,out);
	}

	static SimpleDoubleVec read(final DataInput in) throws IOException {
		return new SimpleDoubleVec(readDoubleArray(in));
	}
}
