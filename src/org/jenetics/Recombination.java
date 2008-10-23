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

import java.util.Random;

import org.jenetics.util.Probability;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Recombination.java,v 1.2 2008-10-23 22:46:06 fwilhelm Exp $
 */
public abstract class Recombination<G extends Gene<?>> extends Alterer<G> {

	/**
	 * Create a <code>Alterer</code> concatenating the given 
	 * <code>Alterer</code>S.
	 * 
	 * @param component the <code>Alterer</code>S this <code>Alterer</code>
	 *        consists.
	 */
	public Recombination(final Alterer<G> component) {
		this(Probability.ONE, component);
	}
	
	/**
	 * Constucts an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws NullPointerException if the <code>probability</code> is 
	 * 		<code>null</code>.
	 */
	public Recombination(final Probability probability) {
		super(probability);
	}
	
	/**
	 * Constructs an alterer with a given recombination probability. A
	 * second Alterer can be specified for a composite Alterer.
	 * 
	 * @param probability The recombination probability.
	 * @param component The composit Alterer.
	 * @throws NullPointerException if the <code>probability</code> or the
	 * 		<code>component</code> is <code>null</code>. 
	 */
	public Recombination(final Probability probability, final Alterer<G> component) {
		super(probability, component);
	}

	@Override
	protected final <C extends Comparable<C>> void change(
		final Population<G, C> population, final int generation
	) {
		final Random random = RandomRegistry.getRandom();
		for (int i = 0, size = population.size(); i < size; ++i) {
			
			//Performing the recombination with the given probability.
			if (_probability.isLargerThan(random.nextDouble())) {
				final int second = random.nextInt(population.size());
				if (second != i) {
					recombinate(population, i, second, generation);
				}
			}
		}
	}
	
	private static void part(int size, final int[] first, final int[] second) {
		final Random random = RandomRegistry.getRandom();
		
		for (int i = 0; i < size; ++i) {
			first[i] = random.nextInt(size);
			second[i] = random.nextInt(size);
		}
	}
	
	/**
	 * Recombination template method.
	 * 
	 * @param <C>
	 * @param population
	 * @param first
	 * @param second
	 * @param generation
	 */
	protected abstract <C extends Comparable<C>> void recombinate(
			Population<G, C> population, int first, int second, int generation
		);
	
	
}






