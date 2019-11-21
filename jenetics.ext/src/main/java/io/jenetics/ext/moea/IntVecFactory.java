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

import java.util.Comparator;

import io.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IntVecFactory implements VecFactory<int[]> {
	private final Optimize[] _optimizes;

	private final ElementComparator<int[]> _comparator;
	private final ElementDistance<int[]> _distance;
	private final Comparator<int[]> _dominance;

	IntVecFactory(final Optimize[] optimizes) {
		Vecs.checkVecLength(optimizes.length);
		_optimizes = optimizes.clone();

		_comparator = this::cmp;
		_distance = this::dist;
		_dominance = this::dom;
	}

	private int cmp(final int[] u, final int[] v, final int i) {
		return _optimizes[i] == Optimize.MAXIMUM
			? Integer.compare(u[i], v[i])
			: Integer.compare(v[i], u[i]);
	}

	private double dist(final int[] u, final int[] v, final int i) {
		return _optimizes[i] == Optimize.MAXIMUM
			? u[i] - v[i]
			: v[i] - u[i];
	}

	private int dom(final int[] u, final int[] v) {
		return Pareto.dominance(u, v, _optimizes.length, this::cmp);
	}

	@Override
	public Vec<int[]> create(final int[] array) {
		return new IntVec(array, _comparator, _distance, _dominance);
	}

}
