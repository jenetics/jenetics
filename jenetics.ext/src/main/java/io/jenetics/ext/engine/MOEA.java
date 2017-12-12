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
package io.jenetics.ext.engine;

import static java.util.Objects.requireNonNull;
import static io.jenetics.ext.util.Pareto.front;

import java.util.Comparator;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.util.ElementComparator;
import io.jenetics.ext.util.ElementDistance;
import io.jenetics.ext.util.ParetoFront;
import io.jenetics.ext.util.Vec;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MOEA {

	private MOEA() {
	}

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
