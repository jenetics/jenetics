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

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class RetryConstraint<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Constraint<G, C>
{

	static final int DEFAULT_RETRY_COUNT = 15;

	private final Predicate<? super Phenotype<G, C>> _validator;
	private final Factory<Genotype<G>> _genotypeFactory;
	private final int _retryLimit;

	RetryConstraint(
		final Predicate<? super Phenotype<G, C>> validator,
		final Factory<Genotype<G>> genotypeFactory,
		final int retryLimit
	) {
		_validator = requireNonNull(validator);
		_genotypeFactory = requireNonNull(genotypeFactory);
		_retryLimit = retryLimit;
	}

	@Override
	public boolean test(final Phenotype<G, C> individual) {
		return _validator.test(individual);
	}

	@Override
	public Phenotype<G, C> repair(final Phenotype<G, C> individual) {
		final long generation = individual.getGeneration();

		int count = 0;
		Phenotype<G, C> phenotype;
		do {
			phenotype = Phenotype.of(_genotypeFactory.newInstance(), generation);
		} while (++count < _retryLimit && !test(phenotype));

		return phenotype;
	}
}
