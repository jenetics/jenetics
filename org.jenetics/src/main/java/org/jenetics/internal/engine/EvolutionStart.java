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

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * Represents a state of the GA.
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
{
	private final Population<G, C> _population;
	private final int _generation;

	public EvolutionStart(final Population<G, C> population, final int generation) {
		_population = requireNonNull(population);
		_generation = generation;
	}

	public Population<G, C> getPopulation() {
		return _population;
	}

	public int getGeneration() {
		return _generation;
	}

	public EvolutionStart<G, C> next(final Population<G, C> population) {
		return new EvolutionStart<>(population, _generation + 1);
	}
}
