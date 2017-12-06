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
package io.jenetics.ext;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

import io.jenetics.ext.util.Pareto;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface NSGA<T> {

	public Comparable<? super T> dominance();



	public static <C extends Comparable<? super C>>
	int[] ranks(final Seq<? extends C> population) {
		return ranks(population, Comparator.naturalOrder());
	}

	public static <T> int[] ranks(
		final Seq<? extends T> population,
		final Comparator<? super T> comparator
	) {
		final Set<T> remaining = new HashSet<>(population.asList());
		final Map<T, Integer> ranks = new HashMap<>();

		int rank = 0;
		while (!remaining.isEmpty()) {
			final ISeq<T> front = Pareto.front(remaining, comparator);

			for (T element : front) {
				remaining.remove(element);
				ranks.put(element, rank);
			}

			rank++;
		}

		final int[] result = new int[population.size()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = ranks.get(population.get(i));
		}

		return result;
	}

	/*
	public static <T> double[] crowdingDistances(
		final Seq<? extends T> front,
		final int dimensions,
		final ComponentComparator<? super T> comparator
	) {
		final double[] distances = new double[front.size()];

		if (distances.length < 3) {
			Arrays.fill(distances, Double.POSITIVE_INFINITY);
		} else {
			Arrays.fill(distances, 0);
			final IndexSorter sorter = IndexSorter.sorter(front.size());
			final int[] indexes = new int[front.size()];

			for (int i = 0; i < dimensions; ++i) {
				final int objective = i;
				sorter.sort(
					front,
					IndexSorter.init(indexes),
					(a, b) -> comparator.compare(a, b, objective)
				);

				distances[indexes[0]] = Double.POSITIVE_INFINITY;
				distances[indexes[indexes.length - 1]] = Double.POSITIVE_INFINITY;

				for (int j = 1; j < indexes.length - 1; ++j) {
					distances[indexes[j]] += 1;
				}
			}
		}

		return distances;
	}
	*/

}
