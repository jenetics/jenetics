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
package io.jenetics;

import static java.util.Objects.requireNonNull;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * The Alterer is responsible for the changing/recombining the Population.
 * Alterers can be chained by appending a list of alterers with the
 * {@link io.jenetics.engine.Engine.Builder#alterers(Alterer, Alterer[])} method.
 *
 * <pre>{@code
 * final Engine<DoubleGene, Double> engine = Engine
 *     .builder(gtf, ff)
 *     .alterers(
 *         new Crossover<>(0.1),
 *         new Mutator<>(0.05),
 *         new MeanAlterer<>(0.2))
 *     .build();
 * final EvolutionStream<DoubleGene, Double> stream = engine.stream();
 * }</pre>
 *
 * The order of the alterer calls is: Crossover, Mutation and MeanAlterer.
 *
 * @param <G> the gene type
 * @param <C> the fitness function result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
@FunctionalInterface
public interface Alterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * The default alter probability: 0.2
	 */
	double DEFAULT_ALTER_PROBABILITY = 0.2;

	/**
	 * Alters (recombine) a given population. If the {@code population} is empty,
	 * nothing is altered. The altered population is part of the returned
	 * {@code AlterResult} object.
	 *
	 * @param population The Population to be altered. If the {@code population}
	 *        is {@code null} or empty, nothing is altered.
	 * @param generation the date of birth (generation) of the altered phenotypes.
	 * @return the alter-result object, which contains the altered population
	 *         and the alteration count
	 * @throws NullPointerException if the given {@code population} is
	 *        {@code null}.
	 */
	AltererResult<G, C> alter(
		final Seq<Phenotype<G, C>> population,
		final long generation
	);

	/**
	 * Returns a composed alterer that first applies the {@code before} alterer
	 * to its input, and then applies {@code this} alterer to the result.
	 *
	 * @param before the alterer to apply first
	 * @return the new composed alterer
	 */
	default Alterer<G, C> compose(final Alterer<G, C> before) {
		return of(requireNonNull(before), this);
	}

	/**
	 * Returns a composed alterer that applies the {@code this} alterer
	 * to its input, and then applies the {@code after} alterer to the result.
	 *
	 * @param after the alterer to apply first
	 * @return the new composed alterer
	 */
	default Alterer<G, C> andThen(final Alterer<G, C> after) {
		return of(this, requireNonNull(after));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @param alterers the alterers to combine.
	 * @return a new alterer which consists of the given one
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	@SafeVarargs
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> of(final Alterer<G, C>... alterers) {
		return alterers.length == 0
			? (p, g) -> new AltererResult<>(p.asISeq(), 0)
			: alterers.length == 1
				? alterers[0]
				: new CompositeAlterer<>(ISeq.of(alterers));
	}

}
