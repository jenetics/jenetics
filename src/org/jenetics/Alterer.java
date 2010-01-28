/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics;


/**
 * Population based methods provide the possibility of incorporating a new set 
 * of moves based on combining solutions. This is referred to as crossover or 
 * recombination. 
 * 
 * The Alterer is responsible for the recombination of a Population. Alterers can
 * be chained by appending an new (component) alterers.
 * 
 * [code]
 *     GeneticAlgorithm<Float64Gene, Double> ga = ...
 *     ga.setAlterer(new CompositeAlterer<Float64Gene>(
 *         new Crossover<Float64Gene>(0.1),
 *         new Mutation<Float64Gene>(0.05)),
 *         new MeanAlterer<Float64eGene>(0.2)
 *     ));
 * [/code]
 * 
 * The order of the alterer calls is: Crossover, Mutation and MeanAlterer.
 * 
 * @param <G> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Alterer.java,v 1.20 2010-01-28 13:03:32 fwilhelm Exp $
 */
public interface Alterer<G extends Gene<?, G>> {

	/**
	 * Alters (recombine) a given population. If the <code>population</code>
	 * is <code>null</code> or empty, nothing is altered.
	 * 
	 * @param population The Population to be altered. If the 
	 *        <code>population</code> is <code>null</code> or empty, nothing is 
	 *        altered.
	 * @param generation the date of birth (generation) of the altered phenotypes.
	 * @throws NullPointerException if the given {@code population} is 
	 *         {@code null}.
	 */
	public <C extends Comparable<C>> void alter(
			final Population<G, C> population, 
			final int generation
		);
	
}
