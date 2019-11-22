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

import static java.lang.String.format;
import static io.jenetics.ext.moea.Vecs.requireVecLength;
import static io.jenetics.ext.moea.Vecs.toFlags;

import java.util.Comparator;
import java.util.List;

import io.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class GeneralLongVecFactory implements VecFactory<long[]> {
	private final boolean[] _maximisations;

	private final ElementComparator<long[]> _comparator = this::cmp;
	private final ElementDistance<long[]> _distance = this::dst;
	private final Comparator<long[]> _dominance = this::dom;

	GeneralLongVecFactory(final List<Optimize> optimizes) {
		Vecs.checkVecLength(optimizes.size());
		_maximisations = toFlags(optimizes);
	}

	private int cmp(final long[] u, final long[] v, final int i) {
		return _maximisations[i]
			? Long.compare(u[i], v[i])
			: Long.compare(v[i], u[i]);
	}

	private double dst(final long[] u, final long[] v, final int i) {
		return _maximisations[i]
			? u[i] - v[i]
			: v[i] - u[i];
	}

	private int dom(final long[] u, final long[] v) {
		return Pareto.dominance(u, v, _maximisations.length, this::cmp);
	}

	@Override
	public Vec<long[]> newVec(final long[] array) {
		requireVecLength(_maximisations.length, array.length);
		return new GeneralLongVec(array, _comparator, _distance, _dominance);
	}

	@Override
	public String toString() {
		return format("VecFactory<long[%d]>", _maximisations.length);
	}

}
