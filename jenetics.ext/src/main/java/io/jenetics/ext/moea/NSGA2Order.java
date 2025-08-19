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
import io.jenetics.util.BaseSeq;
import io.jenetics.util.ProxySorter;

/**
 * NSGA2 crowded distance comparator.
 *
 * @see <a href="https://www.iitk.ac.in/kangal/Deb_NSGA-II.pdf">
 *     A Fast and Elitist Multiobjective Genetic Algorithm: NSGA-II</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.1
 */
final class NSGA2Order<T> implements ProxySorter.Comparator<int[]> {

	private final int[] _ranks;
	private final double[] _distances;

	NSGA2Order(
		final BaseSeq<? extends T> population,
		final Optimize opt,
		final Comparator<? super T> dominance,
		final ElementComparator<? super T> comparator,
		final ElementDistance<? super T> distance,
		final int objectives
	) {
		if (population.isEmpty()) {
			_ranks = new int[0];
			_distances = new double[0];
		} else {
			_ranks = Pareto.ranks(
				population,
				opt == Optimize.MAXIMUM
					? dominance
					: dominance.reversed()
			);

			final var dist = new CrowdingDistance<>(
				opt == Optimize.MAXIMUM
					? comparator
					: comparator.reversed(),
				distance,
				objectives
			);

			_distances = dist.calculate(population);
		}
	}

	@Override
	public int compare(final int[] array, final int i, final int j) {
		return compare(array[i], array[j]);
	}

	int compare(final int i, final int j) {
		if (cco(i, j)) {
			return 1;
		} else if (cco(j, i)) {
			return -1;
		} else {
			return 0;
		}
	}

	private boolean cco(final int i, final int j) {
		return _ranks[i] < _ranks[j] ||
			(_ranks[i] == _ranks[j] && _distances[i] > _distances[j]);
	}
}
