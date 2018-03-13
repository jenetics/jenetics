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
package io.jenetics.engine;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * This interface allows to define different strategies for evaluating the
 * fitness functions of a given population. The implementer is free to do the
 * evaluation <em>in place</em>, or create new {@link Phenotype} instance and
 * return the newly created one. The following code snippet shows how to
 * evaluate the fitness values of the population serially:
 *
 * <pre>{@code
 * ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population) {
 *     population.forEach(Phenotype::evaluate);
 *     return population.asISeq();
 * }
 * }</pre>
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Evaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Evaluates the fitness values of the given {@code population}. A simple
	 * evaluation strategy would evaluate the population in a serial way:
	 *
	 * <pre>{@code
	 * ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population) {
	 *     population.forEach(Phenotype::evaluate);
	 *     return population.asISeq();
	 * }
	 * }</pre>
	 *
	 * @param population the population to evaluate
	 * @return the evaluated population. Implementers are free to return the
	 *         the input population or a newly created one.
	 */
	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population);

	/**
	 * Create a new phenotype evaluator from a given genotype {@code evaluator}.
	 *
	 * @param evaluator the genotype evaluator
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a <em>norma</em> phenotype evaluator from the given genotype
	 *         evaluator
	 * @throws NullPointerException if the given {@code evaluator} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> of(final GenotypeEvaluator<G, C> evaluator) {
		requireNonNull(evaluator);

		return population -> {
			final ISeq<Genotype<G>> genotypes = population.stream()
				.filter(pt -> !pt.isEvaluated())
				.map(Phenotype::getGenotype)
				.collect(ISeq.toISeq());

			if (genotypes.nonEmpty()) {
				final ISeq<C> results = evaluator.evaluate(
					genotypes,
					population.get(0).getFitnessFunction()
				);

				if (genotypes.size() != results.size()) {
					throw new IllegalStateException(format(
						"Expected %d results, but got %d. " +
							"Check your evaluator function.",
						genotypes.size(), results.size()
					));
				}

				final MSeq<Phenotype<G, C>> evaluated = population.asMSeq();
				for (int i = 0, j = 0; i < evaluated.length(); ++i) {
					if (!population.get(i).isEvaluated()) {
						evaluated.set(
							i,
							population.get(i).withFitness(results.get(j++))
						);
					}
				}

				return evaluated.toISeq();
			} else {
				return population.asISeq();
			}
		};
	}

}
