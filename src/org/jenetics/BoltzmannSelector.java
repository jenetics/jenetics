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

import org.jscience.mathematics.number.Number;

/**
 * In this <code>Selector</code>, the probability for selection is defined as:
 * <p/>
 * 
 * <pre>
 *         exp[beta*f_i]
 *  p_i = ---------------,
 *              Z
 * </pre>
 * 
 * where <pre>beta</pre> controls the selection intensity, and<p/>
 * 
 * <pre>
 *     Z = Sum[exp[beta*f_j], j = 1, n].
 * </pre>
 * 
 * f_j denotes the fitness value of the jth individium.
 * 
 * @param <T> the gene type.
 * @param <N> the BoltzmannSelector requires a number type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BoltzmannSelector.java,v 1.5 2008-09-22 21:38:31 fwilhelm Exp $
 */
public class BoltzmannSelector<T extends Gene<?>, N extends Number<N>> 
	extends ProbabilitySelector<T, N> implements Serializable
{
	private static final long serialVersionUID = 4785987330242283796L;
	
	private final double _beta;

	/**
	 * Create a new BolzmanSelecter with a default beta of 0.2.
	 */
	public BoltzmannSelector() {
		this(0.2);
	}
	
	public BoltzmannSelector(final double beta) {
		this._beta = beta;
	}

	@Override
	protected double[] probabilities(final Population<T, N> population, final int count) {
		assert (population != null) : "Population must not be null. ";
		assert (count >= 0) : "Population to select must be greater than zero. ";
		
		final double[] props = new double[population.size()];
		
		double z = 0;
		for (Phenotype<T, N> pt : population) {
			z += exp(_beta*pt.getFitness().doubleValue());
		}
		
		for (int i = 0, n = population.size(); i < n; ++i) {
			props[i] = exp(_beta*population.get(i).getFitness().doubleValue())/z;
		}
	
		return props;
	}

}




