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

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class GeneralDoubleVec implements Vec<double[]> {

	private final double[] _data;
	private final ElementComparator<double[]> _comparator;
	private final ElementDistance<double[]> _distance;
	private final Comparator<double[]> _dominance;

	GeneralDoubleVec(
		final double[] data,
		final ElementComparator<double[]> comparator,
		final ElementDistance<double[]> distance,
		final Comparator<double[]> dominance
	) {
		_data = requireNonNull(data);
		_comparator = requireNonNull(comparator);
		_distance = requireNonNull(distance);
		_dominance = requireNonNull(dominance);
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
		return _comparator;
	}

	@Override
	public ElementDistance<double[]> distance() {
		return _distance;
	}

	@Override
	public Comparator<double[]> dominance() {
		return _dominance;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_data);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof GeneralDoubleVec &&
			Arrays.equals(((GeneralDoubleVec)obj)._data, _data);
	}

	@Override
	public String toString() {
		return Arrays.toString(_data);
	}

}
