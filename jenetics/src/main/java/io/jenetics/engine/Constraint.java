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

import java.util.function.BiFunction;
import java.util.function.Predicate;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

/**
 * This interface allows you to define constraints on single phenotypes. It is a
 * more advanced version of the {@link Phenotype#isValid()} method, which checks
 * the validity of the underlying genotypes and/or chromosomes. Additionally it
 * is possible to <em>repair</em> invalid individuals. The evolution
 * {@link Engine} is using the constraint in the following way: check the validity
 * and repair invalid individuals.
 * <pre>{@code
 * for (int i = 0; i < population.size(); ++i) {
 *     final Phenotype<G, C> individual = population.get(i);
 *     if (!constraint.test(individual)) {
 *         population.set(i, constraint.repair(individual, generation));
 *     }
 * }
 * }</pre>
 *
 * @see Engine.Builder#constraint(Constraint)
 *
 * @apiNote
 * This class is part of the more advanced API and is not needed for default use
 * cases.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public interface Constraint<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Checks the validity of the given {@code individual}.
	 *
	 * @param individual the phenotype to check
	 * @return {@code true} if the given {@code individual} is valid,
	 *         {@code false} otherwise
	 * @throws NullPointerException if the given {@code individual} is
	 *         {@code null}
	 */
	public boolean test(final Phenotype<G, C> individual);

	/**
	 * Tries to repair the given phenotype. This method is called by the
	 * evolution {@link Engine} if the {@link #test(Phenotype)} method returned
	 * {@code false}.
	 *
	 * @param individual the phenotype to repair
	 * @param generation the actual generation used for the repaired phenotype
	 * @return a newly created, valid phenotype. The implementation is free to
	 *         use the given invalid {@code individual} as a starting point for
	 *         the created phenotype.
	 * @throws NullPointerException if the given {@code individual} is
	 *         {@code null}
	 */
	public Phenotype<G, C> repair(
		final Phenotype<G, C> individual,
		final long generation
	);


	/**
	 * Return a new constraint object with the given {@code validator} and
	 * {@code repairer}.
	 *
	 * @param validator the phenotype validator used by the constraint
	 * @param repairer the phenotype repairer used by the constraint
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Constraint<G, C> of(
		final Predicate<? super Phenotype<G, C>> validator,
		final BiFunction<? super Phenotype<G, C>, Long, Phenotype<G, C>> repairer
	) {
		requireNonNull(validator);
		requireNonNull(repairer);

		return new Constraint<G, C>() {
			@Override
			public boolean test(final Phenotype<G, C> individual) {
				return validator.test(individual);
			}

			@Override
			public Phenotype<G, C> repair(
				final Phenotype<G, C> individual,
				final long generation
			) {
				return repairer.apply(individual, generation);
			}
		};
	}

	/**
	 * Return a new constraint object with the given {@code validator}. The used
	 * repairer just creates a new phenotype by using the phenotype to be
	 * repaired as template. The <em>repaired</em> phenotype might still be
	 * invalid.
	 *
	 * @param validator the phenotype validator used by the constraint
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new constraint strategy
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Constraint<G, C> of(final Predicate<? super Phenotype<G, C>> validator) {
		return of(
			validator,
			(pt, gen) -> Phenotype.of(pt.getGenotype().newInstance(), gen)
		);
	}

}
