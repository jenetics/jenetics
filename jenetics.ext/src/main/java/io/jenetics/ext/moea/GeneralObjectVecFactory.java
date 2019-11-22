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

import io.jenetics.Optimize;

import java.util.Comparator;
import java.util.List;

import static io.jenetics.ext.moea.Vecs.requireVecLength;
import static io.jenetics.ext.moea.Vecs.toFlags;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class GeneralObjectVecFactory<T> implements VecFactory<T[]> {
	private final boolean[] _maximisations;
	private final Comparator<? super T> _comp;
	private final ElementDistance<T[]> _dist;

	private final ElementComparator<T[]> _comparator = this::cmp;
	private final ElementDistance<T[]> _distance = this::dst;
	private final Comparator<T[]> _dominance = this::dom;

	GeneralObjectVecFactory(
		final Comparator<? super T> comparator,
		final ElementDistance<T[]> distance,
		final List<Optimize> optimizes
	) {
		Vecs.checkVecLength(optimizes.size());
		_maximisations = toFlags(optimizes);
		_comp = requireNonNull(comparator);
		_dist = requireNonNull(distance);
	}

	private int cmp(final T[] u, final T[] v, final int i) {
		return _maximisations[i]
			? _comp.compare(u[i], v[i])
			: _comp.compare(v[i], u[i]);
	}

	private double dst(final T[] u, final T[] v, final int i) {
		return _maximisations[i]
			? _dist.distance(u, v, i)
			: _dist.distance(v, u, i);
	}

	private int dom(final T[] u, final T[] v) {
		return Pareto.dominance(u, v, _maximisations.length, this::cmp);
	}

	@Override
	public Vec<T[]> newVec(final T[] array) {
		requireVecLength(_maximisations.length, array.length);
		return new GeneralObjectVec<>(array, _comparator, _distance, _dominance);
	}

	@Override
	public String toString() {
		return format("VecFactory<T[%d]>", _maximisations.length);
	}

}
