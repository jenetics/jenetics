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

import static java.lang.Math.pow;

import java.io.Serializable;

/**
 * An alternative to the "weak" <code>LinearRankSelector</code> is to assign
 * survivial probabilities to the sorted individuals using an exponential 
 * function:
 * <p/>
 * <pre>
 *          N-i
 *         c
 *  p_i = -------,
 *           N
 * </pre>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ExponentialRankSelector.java,v 1.8 2009-02-23 20:58:08 fwilhelm Exp $
 */
public class ExponentialRankSelector<G extends Gene<?, G>, C extends Comparable<C>> 
	extends ProbabilitySelector<G, C> implements Serializable
{
	private static final long serialVersionUID = -5633748296591142197L;
	
	private final double _c;
	
	public ExponentialRankSelector(final double c) {
		this._c = c;
	}

	@Override
	protected double[] probabilities(
		final Population<G, C> population, final int count
	) {
		assert(population != null) : "Population can not be null. ";
		assert(count >= 0) : "Population to select must be greater than zero. ";
		
		//Sorted population required.
		population.sort();
		
		final double N = population.size();
		final double[] props = new double[population.size()];
		
		for (int i = 0, n = population.size(); i < N; ++i) {
			props[n - i - 1] = ((_c - 1)*pow(_c, N - i - 1))/(pow(_c, N) - 1);
		}
	
		return props;
	}

}



