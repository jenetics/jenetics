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
package org.jenetics.internal.engine;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-19 $</em>
 */
public final class EvolutionResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final EvolutionDurations _durations;
	private final Population<G, C> _population;
	private final int _generation;

	private EvolutionResult(
		final EvolutionDurations durations,
		final Population<G, C> population,
		final int generation
	) {
		_durations = requireNonNull(durations);
		_population = requireNonNull(population);
		_generation = generation;
	}

	public EvolutionDurations getDurations() {
		return _durations;
	}

	public Population<G, C> getPopulation() {
		return _population;
	}

	public int getGeneration() {
		return _generation;
	}

	public EvolutionStart<G, C> next() {
		return EvolutionStart.of(_population, _generation + 1);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_durations)
			.and(_population)
			.and(_generation).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(result ->
			eq(_durations, result._durations) &&
			eq(_population, result._population) &&
			eq(_generation, result._population)
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final EvolutionDurations durations,
		final Population<G, C> population,
		final int generation
	) {
		return new EvolutionResult<>(durations, population, generation);
	}

}
