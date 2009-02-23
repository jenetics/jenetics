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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: RouletteWheelSelector.java,v 1.9 2009-02-23 20:58:08 fwilhelm Exp $
 */
public class RouletteWheelSelector<G extends Gene<?, G>, N extends Number & Comparable<N>> 
	extends ProbabilitySelector<G, N> implements Serializable
{
	private static final long serialVersionUID = 6434924633105671176L;

	public RouletteWheelSelector() {
	}

	@Override
	protected double[] probabilities(final Population<G, N> population, final int count) {
		assert(population != null) : "Population can not be null. ";
		assert(count >= 0) : "Population to select must be greater than zero. ";
		
		final double[] probabilities = new double[population.size()];
		final double worst = Collections.min(population).getFitness().doubleValue();
		
		double sum = -worst*population.size();
		for (int i = population.size(); --i >= 0;) {
			sum += population.get(i).getFitness().doubleValue();
		}
		
		if (sum > 0.0) {
			for (int i = population.size(); --i >= 0;) {
				probabilities[i] = 
					(population.get(i).getFitness().doubleValue() - worst)/sum;
			}
		} else {
			Arrays.fill(probabilities, 1.0/population.size());
		}
		
		return probabilities;
	}
}





