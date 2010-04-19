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

import java.io.Serializable;

import org.jenetics.util.RandomRegistry;


/**
 * <code>StochasticUniversalSelector</code> is a method for selecting a 
 * population according to some given probability in a way that minimize chance 
 * fluctuations. It can be viewed as a type of roulette game where now we have 
 * P equally spaced points which we spin.
 * <div align="center">
 * 	<img src="doc-files/StochasticUniversalSelector.gif" />
 * </div>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class StochasticUniversalSelector<G extends Gene<?, G>, N extends Number & Comparable<N>> 
	extends RouletteWheelSelector<G, N> implements Serializable 
{
	private static final long serialVersionUID = 3673324276572086631L;

	public StochasticUniversalSelector() {
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#sort()} is called
	 * by this method.)
	 */
	@Override
	public Population<G, N> select(
		final Population<G, N> population, 
		final int count,
		final Optimization opt
	) {
		nonNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " + count
			);
		}
		
		final Population<G, N> selection = new Population<G, N>(count);
		if (count == 0) {
			return selection;
		}
		
		population.sort();
		final double[] probabilities = probabilities(population, count);
		if (opt == Optimization.MINIMIZE) {
			inverse(probabilities);
		}
		assert (population.size() == probabilities.length) :
			"Population size and propability length must be equal.";
		
		//Calculating the equally spaces random points.
		final double delta = 1.0/count;
		final double[] points = new double[count];
		points[0] = RandomRegistry.getRandom().nextDouble()*delta;
		for (int i = 1; i < count; ++i) {
			points[i] = delta*i;
		}
		
		int j = 0;
		double prop = 0;
		for (int i = 0; i < count; ++i) {
			while (points[i] > prop) {
				prop += probabilities[j];
				++j;
			}
			selection.add(population.get(j));
		}

		return selection;
	}

}






