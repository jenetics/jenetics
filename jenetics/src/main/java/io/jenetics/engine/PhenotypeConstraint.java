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

import java.util.function.Predicate;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface PhenotypeConstraint<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	public boolean isValid(final Phenotype<G, C> individual);

	public Phenotype<G, C> newInstance(final long generation);


	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	PhenotypeConstraint<G, C> of(
		final Predicate<? super Phenotype<G, C>> predicate,
		final Factory<Genotype<G>> genotypeFactory,
		final int retryLimit
	) {
		return new PhenotypeConstraint<G, C>() {
			@Override
			public boolean isValid(final Phenotype<G, C> individual) {
				return predicate.test(individual);
			}

			@Override
			public Phenotype<G, C> newInstance(final long generation) {
				int count = 0;
				Phenotype<G, C> phenotype;
				do {
					phenotype = Phenotype.of(genotypeFactory.newInstance(), generation);
				} while (++count < retryLimit && !isValid(phenotype));

				return phenotype;
			}
		};
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	PhenotypeConstraint<G, C> of(
		final Factory<Genotype<G>> genotypeFactory,
		final int retryLimit
	) {
		return of(Phenotype::isValid, genotypeFactory, retryLimit);
	}

}
