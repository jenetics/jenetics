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
import static java.lang.Math.max;

import java.util.Iterator;

import javolution.context.StackContext;
import javolution.xml.XMLSerializable;

import org.jscience.mathematics.number.Number;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: RouletteWheelSelector.java,v 1.3 2008-08-26 22:29:34 fwilhelm Exp $
 */
public class RouletteWheelSelector<G extends Gene<?>, N extends Number<N>> 
	extends ProbabilitySelector<G, N> implements XMLSerializable
{
	private static final long serialVersionUID = 6434924633105671176L;

	public RouletteWheelSelector() {
	}

	@Override
	protected double[] probabilities(final Population<G, N> population, final int count) {
		assert(population != null) : "Population can not be null. ";
		assert(count >= 0) : "Population to select must be greater than zero. ";
		
		final double[] probabilities = new double[population.size()];
		final N worstFitness = population.get(population.size() - 1).getFitness();
		
		StackContext.enter();
		try {
			N sum = null;
			for (Phenotype<G, N> pt : population) {
				if (sum == null) {
					sum = pt.getFitness().minus(worstFitness);
				} else {
					sum = sum.plus(pt.getFitness().minus(worstFitness));
				}
			}
			
			if (abs(sum.doubleValue()) <= 0.0) {
				final double p = 1.0/probabilities.length;
				for (int i = 0; i < probabilities.length; ++i) {
					probabilities[i] = p; 
				}
				return probabilities;
			}
			
			assert(sum.doubleValue() > 0.0);

			int i = 0;
			for (Iterator<Phenotype<G, N>> it = population.iterator(); it.hasNext(); ++i) {
				probabilities[i] = max(0.0, 
						(it.next().getFitness().doubleValue() - 
								worstFitness.doubleValue())/sum.doubleValue()
					);
			}
		} finally {
			StackContext.exit();
		}
		
		return probabilities;
	}
}





