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
import java.util.function.ToIntFunction;

import io.jenetics.Optimize;
import io.jenetics.internal.util.IntComparator;
import io.jenetics.util.BaseSeq;

/**
 * Crowded distance comparator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.1
 */
final class NSGA2Comparator<T> implements IntComparator {

	private final int[] _rank;
	private final double[] _dist;

	NSGA2Comparator(
		final BaseSeq<? extends T> population,
		final Optimize opt,
		final Comparator<? super T> dominance,
		final ElementComparator<? super T> comparator,
		final ElementDistance<? super T> distance,
		final ToIntFunction<? super T> dimension
	) {
		_rank = Pareto.rank(
			population,
			opt == Optimize.MAXIMUM
				? dominance
				: dominance.reversed()
		);

		_dist = Pareto.crowdingDistance(
			population,
			opt == Optimize.MAXIMUM
				? comparator
				: comparator.reversed(),
			distance,
			dimension
		);
	}

	@Override
	public int compare(final int i, final int j) {
		if (cco(i, j)) {
			return 1;
		} else if (cco(j, i)) {
			return -1;
		} else {
			return 0;
		}
	}

	private boolean cco(final int i, final int j) {
		return _rank[i] < _rank[j] ||
			(_rank[i] == _rank[j] && _dist[i] > _dist[j]);
	}

}
