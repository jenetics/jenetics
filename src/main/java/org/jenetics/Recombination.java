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

import static org.jenetics.util.ArrayUtils.shuffle;
import static org.jenetics.util.ArrayUtils.subset;
import static org.jenetics.util.EvaluatorRegistry.evaluate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jenetics.util.Evaluator;
import org.jenetics.util.EvaluatorRegistry;
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
 * @version $Id$
 */
public abstract class Recombination<G extends Gene<?, G>> 
	extends AbstractAlterer<G> 
{
	
	/**
	 * Constructs an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 * 		  valid range of {@code [0, 1]}.
	 */
	public Recombination(final double probability) {
		super(probability);
	}

	/**
	 * This method executes the recombination of in parallel if the current
	 * {@link Evaluator} provides more than one parallel tasks.
	 */
	@Override
	public final <C extends Comparable<C>> void alter(
		final Population<G, C> population, final int generation
	) {
		final int subsetSize = (int)Math.ceil(population.size()*_probability);
		
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
	 * @param population the population to recombine
	 * @param first the source index array.
	 * @param second the target index array.
	 * @param generation the current generation.
	 */
	protected abstract <C extends Comparable<C>> void recombinate(
			Population<G, C> population, int first, int second, int generation
		);
	
	
}






