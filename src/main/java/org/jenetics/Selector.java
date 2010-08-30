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
 * A Selector selects a given number of Chromosomes from the Population.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface Selector<G extends Gene<?, G>, C extends Comparable<C>> {

	/**
	 * Select phenotypes from the Population.
	 * 
	 * @param population The population to select from.
	 * @param count The number of phenotypes to select.
	 * @param opt Determines whether the individuals with higher fitness values
	 * 		 or lower fitness values must be selected. This parameter determines
	 * 		 whether the GA maximizes or minimizes the fitness function.
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
