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
 * Represents a state of the GA at the start of an evolution step.
 *
 * @see EvolutionResult
 * @see EvolutionInit
 * @see EvolutionStreamable#stream(EvolutionStart)
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version !__version__!
 */
public interface EvolutionStart<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {
	/**
	 * Return the population before the evolution step.
	 *
	 * @return the start population
	 */
	public ISeq<Phenotype<G, C>> population();

	/**
	 * Return the generation of the start population.
	 *
	 * @return the start generation
	 */
	public long generation();

	/**
	 * Create a new evolution start object with the given population and for the
	 * given generation.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @param population the start population.
	 * @param generation the start generation of the population
	 * @return a new evolution start object
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStart<G, C> of(
		final Iterable<Phenotype<G, C>> population,
		final long generation
	) {
		return new DefaultEvolutionStart<>(population, generation);
	}

	/**
	 * An empty evolution start object, which can be used as initial evolution
	 * value. The evolution {@link Engine} is then responsible for creating the
	 * proper initial population,
	 *
	 * @since 5.1
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return an empty evolution start object
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStart<G, C> empty() {
		return EvolutionStart.of(ISeq.empty(), 1);
	}

}
