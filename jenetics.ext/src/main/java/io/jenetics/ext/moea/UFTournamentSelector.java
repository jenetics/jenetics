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

import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.math.Combinatorics.subset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * Unique fitness based tournament selection.
 * <p>
 * <em>The selection of unique fitnesses lifts the selection bias towards
 * over-represented fitnesses by reducing multiple solutions sharing the same
 * fitness to a single point in the objective space. It is therefore no longer
 * required to assign a crowding distance of zero to individual of equal fitness
 * as the selection operator correctly enforces diversity preservation by
 * picking unique points in the objective space.</em>
 * <p>
 *  <b>Reference:</b><em>
 *      Félix-Antoine Fortin and Marc Parizeau. 2013. Revisiting the NSGA-II
 *      crowding-distance computation. In Proceedings of the 15th annual
 *      conference on Genetic and evolutionary computation (GECCO '13),
 *      Christian Blum (Ed.). ACM, New York, NY, USA, 623-630.
 *      DOI=<a href="http://dx.doi.org/10.1145/2463372.2463456">
 *          10.1145/2463372.2463456</a></em>
 *
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class UFTournamentSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{
	private final Comparator<Phenotype<G, C>> _dominance;
	private final ElementComparator<Phenotype<G, C>> _comparator;
	private final ElementDistance<Phenotype<G, C>> _distance;
	private final ToIntFunction<Phenotype<G, C>> _dimension;

	/**
	 * Creates a new {@code UFTournamentSelector} with the functions needed for
	 * handling the multi-objective result type {@code C}. For the {@link Vec}
	 * classes, a selector is created like in the following example:
	 * <pre>{@code
	 * new UFTournamentSelector<>(
	 *     Vec<T>::dominance,
	 *     Vec<T>::compare,
	 *     Vec<T>::distance,
	 *     Vec<T>::length
	 * );
	 * }</pre>
	 *
	 * @see #ofVec()
	 *
	 * @param dominance the pareto dominance comparator
	 * @param comparator the vector element comparator
	 * @param distance the vector element distance
	 * @param dimension the dimensionality of vector type {@code C}
	 */
	public UFTournamentSelector(
		final Comparator<? super C> dominance,
		final ElementComparator<? super C> comparator,
		final ElementDistance<? super C> distance,
		final ToIntFunction<? super C> dimension
	) {
		requireNonNull(dominance);
		requireNonNull(comparator);
		requireNonNull(distance);
		requireNonNull(dimension);

		_dominance = (a, b) -> dominance.compare(a.fitness(), b.fitness());
		_comparator = comparator.map(Phenotype::fitness);
		_distance = distance.map(Phenotype::fitness);
		_dimension = v -> dimension.applyAsInt(v.fitness());
	}

	@Override
	public ISeq<Phenotype<G, C>> select(
		final Seq<Phenotype<G, C>> population,
		final int count,
		final Optimize opt
	) {
		final Random random = RandomRegistry.random();

		final CrowdedComparator<Phenotype<G, C>> cc = new CrowdedComparator<>(
			population,
			opt,
			_dominance,
			_comparator,
			_distance,
			_dimension
		);

		final List<Phenotype<G, C>> S = new ArrayList<>();
		while (S.size() < count) {
			final int k = min(2*count - S.size(), population.size());
			final int[] G = subset(population.size(), k, random);

			for (int j = 0; j < G.length - 1 && S.size() < count; j += 2) {
				final int cmp = cc.compare(G[j], G[j + 1]);
				final int p;
				if (cmp > 0) {
					p = G[j];
				} else if (cmp < 0) {
					p = G[j + 1];
				} else {
					p = random.nextBoolean() ? G[j] : G[j + 1];
				}

				final C fitness = population.get(p).fitness();
				final List<Phenotype<G, C>> list = population.stream()
					.filter(pt -> pt.fitness().equals(fitness))
					.collect(Collectors.toList());

				S.add(list.get(random.nextInt(list.size())));
			}
		}

		return ISeq.of(S);
	}

	/**
	 * Return a new selector for the given result type {@code V}. This method is
	 * a shortcut for
	 * <pre>{@code
	 * new UFTournamentSelector<>(
	 *     Vec<T>::dominance,
	 *     Vec<T>::compare,
	 *     Vec<T>::distance,
	 *     Vec<T>::length
	 * );
	 * }</pre>
	 *
	 * @param <G> the gene type
	 * @param <T> the array type, e.g. {@code double[]}
	 * @param <V> the multi object result type vector
	 * @return a new selector for the given result type {@code V}
	 */
	public static <G extends Gene<?, G>, T, V extends Vec<T>>
	UFTournamentSelector<G, V> ofVec() {
		return new UFTournamentSelector<>(
			Vec::dominance,
			Vec::compare,
			Vec::distance,
			Vec::length
		);
	}

}
