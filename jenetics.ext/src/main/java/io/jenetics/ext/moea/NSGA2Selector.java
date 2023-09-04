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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.util.ISeq;
import io.jenetics.util.ProxySorter;
import io.jenetics.util.Seq;

/**
 * This selector selects the first {@code count} elements of the population,
 * which has been sorted by the <em>Crowded-Comparison Operator</em>, as
 * described in <a href="http://ieeexplore.ieee.org/document/996017/">
 *     A Fast and Elitist Multiobjective Genetic Algorithm: NSGA-II</a>
 * <p>
 *  <b>Reference:</b><em>
 *      K. Deb, A. Pratap, S. Agarwal, and T. Meyarivan. 2002. A fast and elitist
 *      multiobjective genetic algorithm: NSGA-II. Trans. Evol. Comp 6, 2
 *      (April 2002), 182-197. DOI=<a href="http://dx.doi.org/10.1109/4235.996017">
 *          10.1109/4235.996017</a></em>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class NSGA2Selector<
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
	 * Creates a new {@code NSGA2Selector} with the functions needed for
	 * handling the multi-objective result type {@code C}. For the {@link Vec}
	 * classes, a selector is created like in the following example:
	 * {@snippet lang="java":
	 * new NSGA2Selector<>(
	 *     Vec<T>::dominance,
	 *     Vec<T>::compare,
	 *     Vec<T>::distance,
	 *     Vec<T>::length
	 * );
	 * }
	 *
	 * @see #ofVec()
	 *
	 * @param dominance the pareto dominance comparator
	 * @param comparator the vector element comparator
	 * @param distance the vector element distance
	 * @param dimension the dimensionality of vector type {@code C}
	 */
	public NSGA2Selector(
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
		final NSGA2Order<Phenotype<G, C>> order = new NSGA2Order<>(
			population,
			opt,
			_dominance,
			_comparator,
			_distance,
			_dimension.applyAsInt(population.get(0))
		);

		final int[] idx = ProxySorter.sort(
			init(new int[population.size()]),
			population.size(),
			order.reversed()
		);

		final List<Phenotype<G, C>> result = new ArrayList<>();
		while (result.size() < count) {
			IntStream.of(idx)
				.limit(count - result.size())
				.mapToObj(population)
				.forEach(result::add);
		}

		return ISeq.of(result);
	}

	private static int[] init(final int[] indexes) {
		for (int i = 0; i < indexes.length; ++i) indexes[i] = i;
		return indexes;
	}

	/**
	 * Return a new selector for the given result type {@code V}. This method is
	 * a shortcut for
	 * {@snippet lang="java":
	 * new NSGA2Selector<>(
	 *     Vec<T>::dominance,
	 *     Vec<T>::compare,
	 *     Vec<T>::distance,
	 *     Vec<T>::length
	 * );
	 * }
	 *
	 * @param <G> the gene type
	 * @param <T> the array type, e.g. {@code double[]}
	 * @param <V> the multi object result type vector
	 * @return a new selector for the given result type {@code V}
	 */
	public static <G extends Gene<?, G>, T, V extends Vec<T>>
	NSGA2Selector<G, V> ofVec() {
		return new NSGA2Selector<>(
			Vec::dominance,
			Vec::compare,
			Vec::distance,
			Vec::length
		);
	}

}
