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

import static java.lang.Math.exp;

import java.io.Serializable;

/**
 * In this <code>Selector</code>, the probability for selection is defined as:
 * <p/><img src="doc-files/boltzmann-formula1.gif" alt="Boltzman" /></p> 
 * where <i>b</i> controls the selection intensity, and
 * <p/><img src="doc-files/boltzmann-formula2.gif" alt="Boltzmann" />.</p> 
 * 
 * <i>f</i><sub><i>j</i></sub> denotes the fitness value of the 
 * <i>j<sup>th</sup></i> individual.
 * <br/>
 * Positive values of <i>b</i> increases the selection probability of the phenotype
 * with high fitness values. Negative values of <i>b</i> increases the selection
 * probability of phenotypes with low fitness values. If <i>b</i> is zero the
 * selection probability of all phenotypes is set to 1/N.
 * 
 * @param <G> the gene type.
 * @param <N> the BoltzmannSelector requires a number type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class BoltzmannSelector<G extends Gene<?, G>, N extends Number & Comparable<N>> 
	extends ProbabilitySelector<G, N> implements Serializable
{
	private static final long serialVersionUID = 4785987330242283796L;
	
	private final double _b;

	/**
	 * Create a new BolzmanSelecter with a default beta of 0.2.
	 */
	public BoltzmannSelector() {
		this(0.2);
	}
	
	/**
	 * Create a new BolzmanSelector with the given <i>b</i> value. High absolute
	 * values of <i>b</i> can create numerical overflows while calculating the 
	 * selection probabilities.
	 * 
	 * @param b the <i>b</i> value of this BolzmanSelector
	 */
	public BoltzmannSelector(final double b) {
		_b = b;
	}

	@Override
	protected double[] probabilities(final Population<G, N> population, final int count) {
		assert (population != null) : "Population must not be null. ";
		assert (count >= 0) : "Population to select must be greater than zero. ";
		
		final double[] props = new double[population.size()];
		
		double z = 0;
		for (Phenotype<G, N> pt : population) {
			z += exp(_b*pt.getFitness().doubleValue());
		}
		
		for (int i = 0, n = population.size(); i < n; ++i) {
			props[i] = exp(_b*population.get(i).getFitness().doubleValue())/z;
		}
	
		return props;
	}

}




