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

import static java.lang.Math.abs;
import static org.jenetics.util.ArrayUtils.sum;
import static org.jenetics.util.BitUtils.ulpDistance;
import static org.jenetics.util.Validator.nonNull;

import java.util.Random;

import org.jenetics.util.RandomRegistry;


/**
 * The ProbabilitySelector selects the new population according the 
 * Probability array the method {@link #probabilities(Population, int)} returns. 
 * The size of the Probability array and the size of the population must be the 
 * same. The order of the population and the probabilities has to be the same too.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class ProbabilitySelector<
	G extends Gene<?, G>, 
	C extends Comparable<? super C>
> 
	implements Selector<G, C> 
{	
	private static final long MAX_ULP_DISTANCE = (long)Math.pow(10, 10);

	protected ProbabilitySelector() {
	}

	@Override
	public Population<G, C> select(
		final Population<G, C> population, 
		final int count,
		final Optimize opt
	) {
		nonNull(population, "Population");
		nonNull(opt, "Optimization");
		if (count < 0) {
			throw new IllegalArgumentException(String.format(
				"Selection count must be greater or equal then zero, but was %s.",
				count
			));
		}
		
		final Population<G, C> selection = new Population<G, C>(count);
		
		if (count > 0) {
			final double[] probabilities = probabilities(population, count, opt);
			
			assert (population.size() == probabilities.length) : 
				"Population size and probability length are not equal.";
			assert (check(probabilities)) : "Probabilities doesn't sum to one.";
			
			final Random random = RandomRegistry.getRandom();
			for (int i = 0; i < count; ++i) {
				selection.add(population.get(nextIndex(probabilities, random)));
			}
			
			assert (count == selection.size());
		}
		
		return selection;
	}
	
	/**
	 * This method takes the probabilities from the 
	 * {@link #probabilities(Population, int)} method and inverts it if needed.
	 * 
	 * @param population The population.
	 * @param count The number of phenotypes to select.
	 * @param opt Determines whether the individuals with higher fitness values
	 * 		 or lower fitness values must be selected. This parameter determines
	 * 		 whether the GA maximizes or minimizes the fitness function. 
	 * @return Probability array.
	 */
	protected final double[] probabilities(
		final Population<G, C> population, 
		final int count,
		final Optimize opt
	) {
		final double[] probabilities = probabilities(population, count);
		if (opt == Optimize.MINIMUM) {
			invert(probabilities);
		}
		return probabilities;
	}
	
	private static void invert(final double[] probabilities) {
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = 1.0 - probabilities[i];
		}
	}
	
	/**
	 * Return an Probability array, which corresponds to the given Population. 
	 * The probability array and the population must have the same size. The 
	 * population is not sorted. If a subclass needs a sorted population, the 
	 * subclass is responsible to sort the population.
	 * <p/>
	 * The implementor always assumes that higher fitness values are better. The
	 * base class inverts the probabilities ({@code p = 1.0 - p }) if the GA is
	 * supposed to minimize the fitness function. 
	 * 
	 * @param population The <em>unsorted</em> population.
	 * @param count The number of phenotypes to select. <i>This parameter is not
	 * 		 needed for most implementations.</i>
	 * @return Probability array. The returned probability array must have the 
	 * 		  length {@code population.size()} and <strong>must</strong> sum to
	 * 		  one. The returned value is checked with 
	 * 		  {@code assert(Math.abs(ArrayUtils.sum(probabilities) - 1.0) < 0.0001)}
	 * 		  in the base class.
	 */
	protected abstract double[] probabilities(
			final Population<G, C> population, 
			final int count
		);
	
	/**
	 * Check if the given probabilities sum to one.
	 * 
	 * @param probabilities the probabilities to check.
	 * @return {@code true} if the sum of the probabilities are within the error
	 * 		  range, {@code false} otherwise.
	 */
	protected static boolean check(final double[] probabilities) {
		final double sum = sum(probabilities);
		boolean check = abs(ulpDistance(sum, 1.0)) < MAX_ULP_DISTANCE;
		
//		if (!check) {
//			System.out.println("Sum: " + sum + "	  " + probabilities.length);
//			System.out.println(Arrays.toString(probabilities));
//		}
		
		return check;
	}
		
	/**
	 * Return the next random index. The index probability is given by the 
	 * {@code probabilities} array. The values of the {@code probabilities} array
	 * must sum to one.
	 * 
	 * @param probabilities the probabilities array (must sum to one).
	 * @param random the random number generator.
	 * @return the random index.
	 */
	static int nextIndex(final double[] probabilities, final Random random) {
		final double prop = random.nextDouble();
		
		int j = 0;
		double sum = 0;
		for (int i = 0; sum < prop && i < probabilities.length; ++i) {
			sum += probabilities[i];
			j = i;
		}
		
		return j;
	}

}







