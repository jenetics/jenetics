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
import static java.util.Objects.requireNonNull;
import static io.jenetics.ext.moea.Pareto.front;

import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * Collectors for collecting final <em>pareto-set</em> for multi-objective
 * fitness function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MOEA {

	private MOEA() {
	}

	/**
	 * Collector of {@link Phenotype} objects, who's (multi-objective) fitness
	 * value is part of the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     pareto front</a>.
	 *
	 * @param <G> the gene type
	 * @param <T> the array type, e.g. {@code double[]}
	 * @param <V> the multi object result type vector
	 * @return the pareto set collector
	 * @throws IllegalArgumentException if the minimal pareto set {@code size}
	 *         is smaller than one
	 */
	public static <G extends Gene<?, G>, T, V extends Vec<T>>
	Collector<EvolutionResult<G, V>, ?, ISeq<Phenotype<G, V>>>
	toParetoSet() {
		return toParetoSet(IntRange.of(75, 100));
	}

	/**
	 * Collector of {@link Phenotype} objects, who's (multi-objective) fitness
	 * value is part of the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     pareto front</a>.
	 *
	 * @param size the allowed size range of the returned pareto set. If the
	 *        size of the pareto set is bigger than {@code size.getMax()},
	 *        during the collection, it is reduced to {@code size.getMin()}.
	 *        Pareto set elements which are close to each other are removed firsts.
	 * @param <G> the gene type
	 * @param <T> the array type, e.g. {@code double[]}
	 * @param <V> the multi object result type vector
	 * @return the pareto set collector
	 * @throws NullPointerException if one the {@code size} is {@code null}
	 * @throws IllegalArgumentException if the minimal pareto set {@code size}
	 *         is smaller than one
	 */
	public static <G extends Gene<?, G>, T, V extends Vec<T>>
	Collector<EvolutionResult<G, V>, ?, ISeq<Phenotype<G, V>>>
	toParetoSet(final IntRange size) {
		return toParetoSet(
			size,
			Vec<T>::dominance,
			Vec<T>::compareTo,
			Vec<T>::distance,
			Vec<T>::length
		);
	}

	/**
	 * Collector of {@link Phenotype} objects, who's (multi-objective) fitness
	 * value is part of the <a href="https://en.wikipedia.org/wiki/Pareto_efficiency">
	 *     pareto front</a>.
	 *
	 * @see #toParetoSet(IntRange)
	 *
	 * @param size the allowed size range of the returned pareto set. If the
	 *        size of the pareto set is bigger than {@code size.getMax()},
	 *        during the collection, it is reduced to {@code size.getMin()}.
	 *        Pareto set elements which are close to each other are removed firsts.
	 * @param dominance the pareto dominance measure of the fitness result type
	 *        {@code C}
	 * @param comparator the comparator of the elements of the vector type
	 *        {@code C}
	 * @param distance the distance function of two elements of the vector
	 *        type {@code C}
	 * @param dimension the dimensionality of the result vector {@code C}.
	 *        Usually {@code Vec::length}.
	 * @param <G> the gene type
	 * @param <C> the multi object result vector. E.g. {@code Vec<double[]>}
	 * @return the pareto set collector
	 * @throws NullPointerException if one the arguments is {@code null}
	 * @throws IllegalArgumentException if the minimal pareto set {@code size}
	 *         is smaller than one
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, ISeq<Phenotype<G, C>>>
	toParetoSet(
		final IntRange size,
		final Comparator<? super C> dominance,
		final ElementComparator<? super C> comparator,
		final ElementDistance<? super C> distance,
		final ToIntFunction<? super C> dimension
	) {
		requireNonNull(size);
		requireNonNull(dominance);
		requireNonNull(distance);
		if (size.getMin() < 1) {
			throw new IllegalArgumentException(format(
				"Minimal pareto set size must be greater than zero: %d",
				size.getMin()
			));
		}

		final UnaryOperator<ParetoFront<Phenotype<G, C>>> shrink = set -> {
			if (set.size() > size.getMax()) {
				set.shrink(
					size.getMin(),
					comparator.map(Phenotype::getFitness),
					distance.map(Phenotype::getFitness),
					v -> dimension.applyAsInt(v.getFitness())
				);
			}
			return set;
		};

		return Collector.of(
			() -> new ParetoFront<Phenotype<G, C>>(
				(a, b) -> dominance.compare(a.getFitness(), b.getFitness())
			),
			(set, result) -> {
				final ISeq<Phenotype<G, C>> front = front(
					result.getPopulation(),
					Phenotype::compareTo
				);
				set.addAll(front.asList());
				shrink.apply(set);
			},
			(a, b) -> shrink.apply(a.merge(b)),
			ParetoFront::toISeq
		);
	}

}
