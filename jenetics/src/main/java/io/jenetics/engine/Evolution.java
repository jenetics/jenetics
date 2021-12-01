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

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;

/**
 * This functional interface defines the evolution function, which takes an
 * {@link EvolutionStart} object, evolves the population, and returns an
 * {@link EvolutionResult} object.
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 5.1
 */
@FunctionalInterface
public interface Evolution<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Perform one evolution step with the given evolution {@code start} object
	 * New phenotypes are created with the fitness function defined by this
	 * <em>engine</em>
	 *
	 * @apiNote
	 * The implementation of this method must be thread-safe.
	 *
	 * @since 3.1
	 * @see #evolve(ISeq, long)
	 *
	 * @param start the evolution start object
	 * @return the evolution result
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}
	 */
	EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start);


	/**
	 * Perform one evolution step with the given {@code population} and
	 * {@code generation}.
	 * <p>
	 * <em>This method is thread-safe.</em>
	 *
	 * @see #evolve(EvolutionStart)
	 *
	 * @param population the population to evolve
	 * @param generation the current generation; used for calculating the
	 *        phenotype age.
	 * @return the evolution result
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	default EvolutionResult<G, C> evolve(
		final ISeq<Phenotype<G, C>> population,
		final long generation
	) {
		return evolve(EvolutionStart.of(population, generation));
	}

}
