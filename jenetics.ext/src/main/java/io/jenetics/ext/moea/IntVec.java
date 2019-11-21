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

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IntVec implements Vec<int[]> {

	private final int[] _data;
	private final ElementComparator<int[]> _comparator;
	private final ElementDistance<int[]> _distance;
	private final Comparator<int[]> _dominance;

	IntVec(
		final int[] data,
		final ElementComparator<int[]> comparator,
		final ElementDistance<int[]> distance,
		final Comparator<int[]> dominance
	) {
		Vecs.checkVecLength(data.length);
		_data = data;
		_comparator = requireNonNull(comparator);
		_distance = requireNonNull(distance);
		_dominance = requireNonNull(dominance);
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
		return _comparator;
	}

	@Override
	public ElementDistance<int[]> distance() {
		return _distance;
	}

	@Override
	public Comparator<int[]> dominance() {
		return _dominance;
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
