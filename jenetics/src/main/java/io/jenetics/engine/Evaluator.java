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

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Evaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population);

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> of(final GenotypeEvaluator<G, C> evaluator) {
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
			}

			return population.asISeq();
		};
	}

}
