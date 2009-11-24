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

import static org.jenetics.util.ArrayUtils.shuffle;
import static org.jenetics.util.ArrayUtils.subset;
import static org.jenetics.util.EvaluatorRegistry.evaluate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jenetics.util.EvaluatorRegistry;
import org.jenetics.util.Probability;
import org.jenetics.util.RandomRegistry;

/**
 * An EGA combine elements of existing solutions in order to create a new solution, 
 * with some of the properties of each parent. Recombination creates a new 
 * chromosome by combining parts of two (or more) parent chromosomes. This 
 * combination of chromosomes can be made by selecting one or more crossover 
 * points, splitting these chromosomes on the selected points, and merge those 
 * portions of different chromosomes to form new ones.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Recombination.java,v 1.15 2009-11-24 22:45:36 fwilhelm Exp $
 */
public abstract class Recombination<G extends Gene<?, G>> extends Alterer<G> {

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
	public Recombination(
		final Probability probability, final Alterer<G> component
	) {
		super(probability, component);
	}

	@Override
	protected final <C extends Comparable<C>> void change(
		final Population<G, C> population, final int generation
	) {
		final int subsetSize = (int)Math.ceil(population.size()*_probability.doubleValue());
		
		if (subsetSize > 0) {
			final Random random = RandomRegistry.getRandom();
			final int[] first = subset(population.size(), subsetSize, random);
			final int[] second = subset(population.size(), subsetSize, random);
			shuffle(second, random);
			
			if (EvaluatorRegistry.getParallelTasks() > 1) {
				final List<Runnable> tasks = new ArrayList<Runnable>(subsetSize);
				for (int i = 0; i < subsetSize; ++i) {
					final int index = i;
					tasks.add(new Runnable() { @Override public void run() {
						recombinate(
								population, 
								first[index], 
								second[index], 
								generation
							);
					}});
				}
				evaluate(tasks);
			} else {
				for (int i = 0; i < subsetSize; ++i) {
					recombinate(population, first[i], second[i], generation);
				}
			}
		}
	}
	
	/**
	 * Recombination template method.
	 * 
	 * @param <C> the fitness result type
	 * @param population the population to recombinate
	 * @param first the source index array.
	 * @param second the target index array.
	 * @param generation the current generation.
	 */
	protected abstract <C extends Comparable<C>> void recombinate(
			Population<G, C> population, int first, int second, int generation
		);
	
	
}






