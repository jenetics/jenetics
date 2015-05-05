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
package org.jenetics;

/**
 * Selectors are responsible for selecting a given number of individuals from
 * the population. The selectors are used to divide the population into
 * survivors and offspring. The selectors for offspring and for the survivors
 * can be chosen independently.
 * <pre>{@code
 * final Engine<DoubleGene, Double> engine = Engine
 *     .builder(gtf, ff)
 *     .offspringSelector(new RouletteWheelSelector<>())
 *     .survivorsSelector(new TournamentSelector<>())
 *     .build();
 * }</pre>
 *
 * @param <G> The gene type this GA evaluates,
 * @param <C> The result type (of the fitness function).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0
 */
@FunctionalInterface
public interface Selector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	/**
	 * Select phenotypes from the Population.
	 *
	 * @param population The population to select from.
	 * @param count The number of phenotypes to select.
	 * @param opt Determines whether the individuals with higher fitness values
	 *        or lower fitness values must be selected. This parameter determines
	 *        whether the GA maximizes or minimizes the fitness function.
	 * @return The selected phenotypes (a new Population).
	 * @throws NullPointerException if the arguments is {@code null}.
	 * @throws IllegalArgumentException if the select count is smaller than zero.
	 */
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	);

}
