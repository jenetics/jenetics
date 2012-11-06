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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;


/**
 * The Alterer is responsible for the changing/recombining the Population.
 * Alterers can be chained by appending a list of alterers with the
 * {@link GeneticAlgorithm#setAlterers(Alterer...)} method.
 *
 * [code]
 * GeneticAlgorithm<Float64Gene, Float64> ga = ...
 * ga.setAlterers(
 *     new Crossover<Float64Gene>(0.1),
 *     new Mutator<Float64Gene>(0.05),
 *     new MeanAlterer<Float64Gene>(0.2)
 * );
 * [/code]
 *
 * The order of the alterer calls is: Crossover, Mutation and MeanAlterer.
 *
 * @param <G> the gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; $Date$
 */
public interface Alterer<G extends Gene<?, G>> {


	/**
	 * Alters (recombine) a given population. If the <code>population</code>
	 * is empty, nothing is altered.
	 *
	 * @param population The Population to be altered. If the
	 *         <code>population</code> is <code>null</code> or empty, nothing is
	 *         altered.
	 * @param generation the date of birth (generation) of the altered phenotypes.
	 * @return the number of genes that has been altered.
	 * @throws NullPointerException if the given {@code population} is
	 *        {@code null}.
	 */
	public <C extends Comparable<? super C>> int alter(
		final Population<G, C> population,
		final int generation
	);

}



