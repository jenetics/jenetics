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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.engine;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.require;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * Represents a state of the GA at the start of an evolution step.
 *
 * @see EvolutionResult
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version 3.1
 */
public final class EvolutionStart<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final Population<G, C> _population;
	private final long _generation;

	private EvolutionStart(
		final Population<G, C> population,
		final long generation
	) {
		_population = requireNonNull(population);
		_generation = require.positive(generation);
	}

	/**
	 * Return the population before the evolution step.
	 *
	 * @return the start population
	 */
	public Population<G, C> getPopulation() {
		return _population;
	}

	/**
	 * Return the generation of the start population.
	 *
	 * @return the start generation
	 */
	public long getGeneration() {
		return _generation;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_population)
			.and(_generation).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EvolutionStart<?, ?> &&
			eq(_generation, ((EvolutionStart<?, ?>)obj)._generation) &&
			eq(_population, ((EvolutionStart<?, ?>)obj)._population);
	}

	@Override
	public String toString() {
		return format(
			"EvolutionStart[population-size=%d, generation=%d]",
			_population.size(), _generation
		);
	}

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
		final Population<G, C> population,
		final long generation
	) {
		return new EvolutionStart<>(population, generation);
	}

}
