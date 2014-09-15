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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * Represents a state of the GA at the start of an evolution step.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class EvolutionStart<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Population<G, C> _population;
	private final int _generation;

	private EvolutionStart(
		final Population<G, C> population,
		final int generation
	) {
		_population = requireNonNull(population);
		_generation = generation;
	}

	/**
	 * Return the start population.
	 *
	 * @return the start population
	 */
	public Population<G, C> getPopulation() {
		return _population;
	}

	/**
	 * Return the current generation.
	 *
	 * @return the current generation
	 */
	public int getGeneration() {
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
		return Equality.of(this, obj).test(start ->
			eq(_population, start._population) &&
			eq(_generation, start._generation)
		);
	}

	/**
	 * Create an new {@code EvolutionStart} object from the given values.
	 *
	 * @param population the start population
	 * @param generation the current generation
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a new evolution start object
	 * @throws java.lang.NullPointerException if the give {@code population} is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStart<G, C> of(
		final Population<G, C> population,
		final int generation
	) {
		return new EvolutionStart<>(population, generation);
	}
}
