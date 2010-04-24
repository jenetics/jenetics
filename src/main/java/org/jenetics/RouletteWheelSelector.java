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

import static java.lang.Math.abs;
import static org.jenetics.util.BitUtils.ulpDistance;

import java.io.Serializable;
import java.util.Arrays;

import org.jenetics.util.ArrayUtils;

/**
 * @see <a href="http://en.wikipedia.org/wiki/Roulette_wheel_selection">
 *          Roulette Wheel Selection
 *      </a>
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class RouletteWheelSelector<G extends Gene<?, G>, N extends Number & Comparable<N>> 
	extends ProbabilitySelector<G, N> implements Serializable
{
	private static final long serialVersionUID = 6434924633105671176L;
	
	private static final long MAX_ULP_DISTANCE = (long)Math.pow(10, 9);

	public RouletteWheelSelector() {
	}

	@Override
	protected double[] probabilities(
		final Population<G, N> population, 
		final int count
	) {
		assert(population != null) : "Population can not be null. ";
		assert(count >= 0) : "Population to select must be greater than zero. ";
		
		final double[] probabilities = new double[population.size()];
		for (int i = population.size(); --i >= 0;) {
			probabilities[i] = population.get(i).getFitness().doubleValue();
		}
		
		final double worst = ArrayUtils.min(probabilities);
		double sum = ArrayUtils.sum(probabilities) - worst*population.size();
		
		if (abs(ulpDistance(sum, 0.0)) > MAX_ULP_DISTANCE) {
			for (int i = population.size(); --i >= 0;) {
				probabilities[i] = (probabilities[i] - worst)/sum;
			}
		} else {
			Arrays.fill(probabilities, 1.0/population.size());
		}
		
		assert (check(probabilities)) : "Probabilities doesn't sum to one.";
		return probabilities;
	}
}





