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

import java.util.Objects;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.internal.util.require;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class EvolutionInit<G extends Gene<?, G>> {

	private final ISeq<Genotype<G>> _population;
	private final long _generation;

	private EvolutionInit(
		final ISeq<Genotype<G>> population,
		final long generation
	) {
		_population = requireNonNull(population);
		_generation = require.positive(generation);
	}

	/**
	 * Return the initial population.
	 *
	 * @return the initial population
	 */
	public ISeq<Genotype<G>> getPopulation() {
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
		int hash = 17;
		hash += 31*_generation + 17;
		hash += 31*Objects.hashCode(_population) + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EvolutionInit<?> &&
			_generation == ((EvolutionInit<?>)obj)._generation &&
			Objects.equals(_population, ((EvolutionInit<?>)obj)._population);
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
	 * @param population the start population.
	 * @param generation the start generation of the population
	 * @return a new evolution start object
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public static <G extends Gene<?, G>>
	EvolutionInit<G> of(
		final ISeq<Genotype<G>> population,
		final long generation
	) {
		return new EvolutionInit<>(population, generation);
	}

}
