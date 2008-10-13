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

import org.jenetics.util.Validator;


/**
 * The ProbabilitySelector selects the new population according the 
 * Probability array the method getProbabilties returns. The size of
 * the Probability array and the size of the population must be the same.
 * The order of the population and the probabilities has to be the same too.
 * The probabilities in the array must sum to one!
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ProbabilitySelector.java,v 1.7 2008-10-13 19:15:01 fwilhelm Exp $
 */
public abstract class ProbabilitySelector<G extends Gene<?>, C extends Comparable<C>> 
	implements Selector<G, C> 
{
	private static final long serialVersionUID = -2980541308499034709L;

	protected ProbabilitySelector() {
	}

	@Override
	public Population<G, C> select(final Population<G, C> population, final int count) {
		Validator.notNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " + count
			);
		}
		
		final Population<G, C> selection = new Population<G, C>(count);
		if (count == 0) {
			return selection;
		}
		
		final double[] probabilities = probabilities(population, count);
		assert (population.size() == probabilities.length);
		final Random random = RandomRegistry.getRandom();
		
		for (int i = 0; i < count; ++i) {
			selection.add(population.get(nextIndex(probabilities, random)));
		}
		
		assert(count == selection.size());
		return selection;
	}
	
	/**
	 * Return a Probability array, which corresponds to the given 
	 * Population. The probability array and the population must have the same
	 * size. The population is not sorted. If a subclass needs a sorted 
	 * population, the subclass is responsible to sort the population.
	 * 
	 * @param population The <em>unsorted</em> population.
	 * @param count 
	 * @return Probability array.
	 */
	protected abstract double[] probabilities(
		final Population<G, C> population, final int count
	);
	
	/**
	 * Return the next random index. The index probability is given by the 
	 * {@code probabilities} array. The values of the {@code probabilities} array
	 * must sum to one.
	 * 
	 * @param probabilities the probabilities array (must sum to one).
	 * @param random the random number generator.
	 * @return the random index.
	 */
	public static int nextIndex(final double[] probabilities, final Random random) {
		final double prop = random.nextDouble();
		
		int j = -1;
		double sum = 0;
		do {
			++j;
			sum += probabilities[j]; 
		} while (j < probabilities.length && sum < prop);
		
		return j;
	}

}







