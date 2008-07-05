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


/**
 * The ProbabilitySelector selects the new population according the 
 * Probability array the method getProbabilties returns. The size of
 * the Probability array and the size of the population must be the same.
 * The order of the population and the probabilities has to be the same too.
 * The probabilities in the array must sum to one!
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ProbabilitySelector.java,v 1.2 2008-07-05 20:28:11 fwilhelm Exp $
 */
public abstract class ProbabilitySelector<T extends Gene<?>> implements Selector<T> {
	private static final long serialVersionUID = -2980541308499034709L;

	protected ProbabilitySelector() {
	}

	@Override
	public Population<T> select(final Population<T> population, final int count) {
		Checker.checkNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " + count
			);
		}
		
		final Population<T> selection = new Population<T>(count);
		if (count == 0) {
			return selection;
		}
		
		population.sort();
		final Probability[] probabilities = probabilities(population, count);
		assert (population.size() == probabilities.length);
		final Random random = RandomRegistry.getRandom();
		
		for (int i = 0; i < count; ++i) {
			double prop = random.nextDouble();
			int j = -1;
			double sum = 0;
			do {
				++j;
				sum += probabilities[j].doubleValue(); 
			} while (j < probabilities.length && sum < prop);
			
			selection.add(population.get(j));
		}
		
		assert(count == selection.size());
		return selection;
	}
	
	/**
	 * Return a Probability array, which corresponds to the given 
	 * Population. The pobability array and the population must have the same
	 * size.
	 * 
	 * @param population The population, which has been sortet ascending 
	 * 	  according to the fitness value.
	 * @param count 
	 * @return Probability array.
	 */
	protected abstract Probability[] probabilities(
		final Population<T> population, final int count
	);

}







