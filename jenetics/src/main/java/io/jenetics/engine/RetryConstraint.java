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
 * This simple {@code Constraint} implementation <em>repairs</em> an invalid
 * phenotype by creating new individuals until a valid one has been created.
 *
 * @apiNote
 * This class is part of the more advanced API and is not needed for default use
 * cases.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class RetryConstraint<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Constraint<G, C>
{

	/**
	 * The default retry-count for creating new, valid phenotypes.
	 */
	public static final int DEFAULT_RETRY_COUNT = 10;

	private final Predicate<? super Phenotype<G, C>> _validator;
	private final Factory<Genotype<G>> _genotypeFactory;
	private final int _retryLimit;

	/**
	 * Create a new retry-constraint with the given parameters.
	 *
	 * @param validator the phenotype validator
	 * @param genotypeFactory the genotype factory used for creating new
	 *        phenotypes. The genotype factory may be {@code null}. In this case,
	 *        the phenotype to be repaired is used as template.
	 * @param retryLimit the limit of the phenotype creation retries. If more
	 *        re-creation tries are necessary, an invalid phenotype is returned.
	 *        This limit guarantees the termination of the
	 *        {@link #repair(Phenotype,long)} method.
	 * @throws NullPointerException if the {@code validator} is {@code null}
	 */
	public RetryConstraint(
		final Predicate<? super Phenotype<G, C>> validator,
		final Factory<Genotype<G>> genotypeFactory,
		final int retryLimit
	) {
		_validator = requireNonNull(validator);
		_genotypeFactory = genotypeFactory;
		_retryLimit = retryLimit;
	}

	@Override
	public boolean test(final Phenotype<G, C> individual) {
		return _validator.test(individual);
	}

	@Override
	public Phenotype<G, C> repair(
		final Phenotype<G, C> individual,
		final long generation
	) {
		final Factory<Genotype<G>> gtf = _genotypeFactory != null
			? _genotypeFactory
			: individual.getGenotype();

		int count = 0;
		Phenotype<G, C> phenotype;
		do {
			phenotype = Phenotype.of(gtf.newInstance(), generation);
		} while (++count < _retryLimit && !test(phenotype));

		return phenotype;
	}

	/**
	 * Return a new constraint with the given genotype factory. The phenotype
	 * validator is set to {@link Phenotype#isValid()} and the retry count to
	 * {@link #DEFAULT_RETRY_COUNT}.
	 *
	 * @param genotypeFactory the genotype factory used for creating new
	 *        phenotypes
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	RetryConstraint<G, C> of(final Factory<Genotype<G>> genotypeFactory) {
		return new RetryConstraint<>(
			Phenotype::isValid,
			genotypeFactory,
			DEFAULT_RETRY_COUNT
		);
	}

	/**
	 * Return a new constraint with the given {@code validator} and the
	 * {@link #DEFAULT_RETRY_COUNT}.
	 *
	 * @param validator the phenotype validator
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	RetryConstraint<G, C> of(final Predicate<? super Phenotype<G, C>> validator) {
		return new RetryConstraint<>(
			validator,
			null,
			DEFAULT_RETRY_COUNT
		);
	}

	/**
	 * Return a new constraint with the given {@code validator} and
	 * {@code retryLimit}.
	 *
	 * @param validator the phenotype validator
	 * @param retryLimit the limit of the phenotype creation retries. If more
	 *        re-creation tries are necessary, an invalid phenotype is returned.
	 *        This limit guarantees the termination of the
	 *        {@link #repair(Phenotype, long)} method.
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	RetryConstraint<G, C> of(
		final Predicate<? super Phenotype<G, C>> validator,
		final int retryLimit
	) {
		return new RetryConstraint<>(
			validator,
			null,
			retryLimit
		);
	}

}
