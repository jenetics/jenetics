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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

import io.jenetics.ext.util.ComponentComparator;
import io.jenetics.ext.util.ParetoSet;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Pareto {

	private Pareto() {
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, ISeq<Phenotype<G, C>>>
	toParetoSet(
		final IntRange capacity,
		final Comparator<? super Phenotype<G, C>> dominance,
		final Function<? super List<Phenotype<G, C>>, double[]> distance
	) {
		return Collector.of(
			() -> new ParetoSet<>(capacity, dominance, distance),
			(set, result) -> {
				if (set.isEmpty()) set.addAll(result.getPopulation());
				else set.add(result.getBestPhenotype());
			},
			ParetoSet::merge,
			ParetoSet::toISeq
		);
	}

	/**
	 * Return the elements, from the given input {@code set}, which are part of
	 * the pareto front. The {@link Comparable} interface defines the dominance
	 * measure of the elements, used for calculating the pareto front.
	 * <p>
	 *  <b>Reference:</b><em>
	 *      E. Zitzler and L. Thiele,
	 *      Multiobjective Evolutionary Algorithms: A Comparative Case Study
	 *      and the Strength Pareto Approach,
	 *      IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
	 *      pp. 257-271, 1999.</em>
	 *
	 * @param set the input set
	 * @param <C> the element type
	 * @return the elements which are part of the pareto set
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C extends Comparable<? super C>> ISeq<C>
	front(final Iterable<? extends C> set) {
		return front(set, Comparator.naturalOrder());
	}

	/**
	 * Return the elements, from the given input {@code set}, which are part of
	 * the pareto front.
	 * <p>
	 *  <b>Reference:</b><em>
	 *      E. Zitzler and L. Thiele,
	 *      Multiobjective Evolutionary Algorithms: A Comparative Case Study
	 *      and the Strength Pareto Approach,
	 *      IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
	 *      pp. 257-271, 1999.</em>
	 *
	 * @param set the input set
	 * @param dominance the dominance comparator used
	 * @param <T> the element type
	 * @return the elements which are part of the pareto set
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> ISeq<T> front(
		final Iterable<? extends T> set,
		final Comparator<? super T> dominance
	) {
		final MSeq<T> front = MSeq.of(set);

		int n = front.size();
		int i = 0;
		while (i < n) {
			int j = i + 1;
			while (j < n) {
				if (dominance.compare(front.get(i), front.get(j)) > 0) {
					--n;
					front.swap(j, n);
				} else if (dominance.compare(front.get(j), front.get(i)) > 0) {
					--n;
					front.swap(i, n);
					--i;
					break;
				} else {
					++j;
				}
			}
			++i;
		}

		return front.subSeq(0, n).copy().toISeq();
	}

	public static <C extends Comparable<? super C>>
	int[] ranks(final Seq<? extends C> population) {
		return ranks(population, Comparator.naturalOrder());
	}

	public static <T> int[] ranks(
		final Seq<? extends T> population,
		final Comparator<? super T> comparator
	) {
		final int n = population.size();

		final int[] ranks = new int[n];

		// Pre-compute the dominance relations
		final int[][] dominanceChecks = new int[n][n];

		for (int i = 0; i < n; i++) {
			final T si = population.get(i);

			for (int j = i + 1; j < n; j++) {
				if (i != j) {
					final T sj = population.get(j);

					dominanceChecks[i][j] = comparator.compare(si, sj);
					dominanceChecks[j][i] = -dominanceChecks[i][j];
				}
			}
		}

		// compute for each solution s_i the solutions s_j that it dominates
		// and the number of times it is dominated
		int[] dominatedCounts = new int[n];
		List<List<Integer>> dominatesList = new ArrayList<>();
		List<Integer> currentFront = new ArrayList<>();


		for (int i = 0; i < n; i++) {
			List<Integer> dominates = new ArrayList<>();
			int dominatedCount = 0;

			for (int j = 0; j < n; j++) {
				if (i != j) {
					if (dominanceChecks[i][j] < 0) {
						dominates.add(j);
					} else if (dominanceChecks[j][i] < 0) {
						dominatedCount += 1;
					}
				}
			}

			if (dominatedCount == 0) {
				currentFront.add(i);
			}

			dominatesList.add(dominates);
			dominatedCounts[i] = dominatedCount;
		}

		// assign ranks
		int rank = 0;

		while (!currentFront.isEmpty()) {
			List<Integer> nextFront = new ArrayList<>();
			List<T> solutionsInFront = new ArrayList<>();

			for (int i = 0; i < currentFront.size(); i++) {
				final T element = population.get(currentFront.get(i));
				//Solution solution = population.get(currentFront.get(i));
				//solution.setAttribute(RANK_ATTRIBUTE, rank);

				ranks[currentFront.get(i)] = rank;

				// update the dominated counts as compute next front
				for (Integer j : dominatesList.get(currentFront.get(i))) {
					dominatedCounts[j] -= 1;

					if (dominatedCounts[j] == 0) {
						nextFront.add(j);
					}
				}

				solutionsInFront.add(element);
			}

			//updateCrowdingDistance(solutionsInFront);

			rank += 1;
			currentFront = nextFront;
		}

		return ranks;
	}


	/* *************************************************************************
	 * 'dominates'
	 * ************************************************************************/

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param <C> the element type of vector <b>u</b> and <b>v</b>
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static <C extends Comparable<? super C>> int
	dominates(final C[] u, final C[] v) {
		return dominates(u, v, Comparator.naturalOrder());
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param comparator the element comparator which is used for calculating
	 *        the dominance
	 * @param <T> the element type of vector <b>u</b> and <b>v</b>
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static <T> int
	dominates(final T[] u, final T[] v, final Comparator<? super T> comparator) {
		requireNonNull(comparator);
		checkLength(u.length, v.length);

		return dominates(
			u, v, u.length,
			(a, b, i) -> comparator.compare(a[i], b[i])
		);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static int dominates(final int[] u, final int[] v) {
		checkLength(u.length, v.length);

		return dominates(
			u, v, u.length,
			(a, b, i) -> Integer.compare(a[i], b[i])
		);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static int dominates(final long[] u, final long[] v) {
		checkLength(u.length, v.length);

		return dominates(
			u, v, u.length,
			(a, b, i) -> Long.compare(a[i], b[i])
		);
	}

	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     <b>Pareto Dominance</b></a> of the two vectors <b>u</b> and <b>v</b>.
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @return {@code 1} if <b>u</b> ≻ <b>v</b>, {@code -1} if <b>v</b> ≻
	 *         <b>u</b> and {@code 0} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code u.length != v.length}
	 */
	public static int dominates(final double[] u, final double[] v) {
		checkLength(u.length, v.length);

		return dominates(
			u, v, u.length,
			(a, b, i) -> Double.compare(a[i], b[i])
		);
	}

	private static void checkLength(final int i, final int j) {
		if (i != j) {
			throw new IllegalArgumentException(format(
				"Length are not equals: %d != %d.", i, j
			));
		}
	}

	private static <T> int dominates(
		final T u,
		final T v,
		final int length,
		final ComponentComparator<? super T> comparator
	) {
		requireNonNull(comparator);

		boolean udominated = false;
		boolean vdominated = false;

		for (int i = 0; i < length; ++i) {
			final int cmp = comparator.compare(u, v, i);

			if (cmp > 0) {
				udominated = true;
				if (vdominated) {
					return 0;
				}
			} else if (cmp < 0) {
				vdominated = true;
				if (udominated) {
					return 0;
				}
			}
		}

		if (udominated == vdominated) {
			return 0;
		} else if (udominated) {
			return 1;
		} else {
			return -1;
		}
	}

}
