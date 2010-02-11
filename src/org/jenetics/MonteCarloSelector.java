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

import static org.jenetics.util.Validator.nonNull;

import java.util.Random;

import org.jenetics.util.RandomRegistry;

/**
 * Selects the phenotypes from a given population randomly. This class can be
 * used to measure the performance of an other given selector.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class MonteCarloSelector<G extends Gene<?,G>, C extends Comparable<C>> 
	implements Selector<G, C> 
{
	private static final long serialVersionUID = -6012139083329541651L;

	@Override
	public Population<G, C> select(final Population<G, C> population, final int count) {
		nonNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(String.format(
				"Selection count must be greater or equal then zero, but was %s.",
				count
			));
		}
		
		final Population<G, C> selection = new Population<G, C>(count);
		
		if (count > 0) {
			final Random random = RandomRegistry.getRandom();
			final int size = population.size();
			for (int i = 0; i < count; ++i) {
				final int pos = random.nextInt(size);
				selection.add(population.get(pos));
			}
		}
		
		return selection;
	}

}
