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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * {@link Vec} implementations for basic array types.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
final class Vecs {

	private Vecs() {
	}

	private static void checkVecLength(final int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Array length must greater zero.");
		}
	}

	static final class ObjectVec<T> implements Vec<T[]> {
		private final T[] _data;
		private final Comparator<? super T> _comparator;
		private final ElementDistance<T[]> _distance;

		ObjectVec(
			final T[] data,
			final Comparator<? super T> comparator,
			final ElementDistance<T[]> distance
		) {
			checkVecLength(data.length);
			_data = data;
			_comparator = requireNonNull(comparator);
			_distance = requireNonNull(distance);
		}

		@Override
		public T[] data() {
			return _data;
		}

		@Override
		public int length() {
			return _data.length;
		}

		@Override
		public ElementComparator<T[]> comparator() {
			return (u, v, i) -> _comparator.compare(u[i], v[i]);
		}

		@Override
		public ElementDistance<T[]> distance() {
			return _distance;
		}

		@Override
		public Comparator<T[]> dominance() {
			return (u, v) -> Vec.dominance(u, v, _comparator);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_data);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof ObjectVec &&
				Arrays.equals(((ObjectVec)obj)._data, _data);
		}

		@Override
		public String toString() {
			return Arrays.toString(_data);
		}

	}

	static final class IntVec implements Vec<int[]>, Serializable {
		private static final long serialVersionUID = 1L;

		private final int[] _data;

		IntVec(final int[] data) {
			checkVecLength(data.length);
			_data = data;
		}

		@Override
		public int[] data() {
			return _data;
		}

		@Override
		public int length() {
			return _data.length;
		}

		@Override
		public ElementComparator<int[]> comparator() {
			return (u, v, i) -> Integer.compare(u[i], v[i]);
		}

		@Override
		public ElementDistance<int[]> distance() {
			return (u, v, i) -> u[i] - v[i];
		}

		@Override
		public Comparator<int[]> dominance() {
			return Vec::dominance;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_data);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof IntVec &&
				Arrays.equals(((IntVec)obj)._data, _data);
		}

		@Override
		public String toString() {
			return Arrays.toString(_data);
		}

	}

	static final class LongVec implements Vec<long[]>, Serializable {
		private static final long serialVersionUID = 1L;

		private final long[] _data;

		LongVec(final long[] data) {
			checkVecLength(data.length);
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
			return (u, v, i) -> Long.compare(u[i], v[i]);
		}

		@Override
		public ElementDistance<long[]> distance() {
			return (u, v, i) -> u[i] - v[i];
		}

		@Override
		public Comparator<long[]> dominance() {
			return Vec::dominance;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_data);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof LongVec &&
				Arrays.equals(((LongVec)obj)._data, _data);
		}

		@Override
		public String toString() {
			return Arrays.toString(_data);
		}

	}

	static final class DoubleVec implements Vec<double[]>, Serializable {
		private static final long serialVersionUID = 1L;

		private final double[] _data;

		DoubleVec(final double[] data) {
			checkVecLength(data.length);
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
				Arrays.equals(((DoubleVec)obj)._data, _data);
		}

		@Override
		public String toString() {
			return Arrays.toString(_data);
		}

	}


}
