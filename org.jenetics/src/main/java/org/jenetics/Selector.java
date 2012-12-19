/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

/**
 * Selectors are responsible for selecting a given number of individuals from
 * the population. The selectors are used to divide the population into
 * survivors and offspring. The selectors for offspring and for the survivors
 * can be chosen independently.
 * [code]
 * GeneticAlgorithm<Float64Gene, Float64> ga = ...
 * ga.setOffspringFraction(0.7);
 * ga.setSurvivorSelector(
 *     new RouletteWheelSelector<Float64Gene, Float64>()
 * );
 * ga.setOffspringSelector(
 *     new TournamentSelector<Float64Gene, Float64>()
 * );
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
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
	 *         or lower fitness values must be selected. This parameter determines
	 *         whether the GA maximizes or minimizes the fitness function.
	 * @return The selected phenotypes (a new Population).
	 * @throws NullPointerException if the arguments is <code>null</code>.
	 * @throws IllegalArgumentException if the select count is smaller than zero.
	 */
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	);

}
