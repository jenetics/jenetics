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


/**
 * In linear ranking selection, individuals (phenotypes) are sorted according
 * to their fitness values and the rank N is assignee to the best Phenotype 
 * individual and the rank 1 to the worst Phenotype. The selection probability 
 * is linearly assigned to the individuals according to their rank:<p/>
 * <p/><img src="doc-files/linear-rank-selector.gif" alt="Linear Rank Selection" /></p> 
 * 
 * Here <i>n</i><sub><i>m</i></sub>/<i>N</i> is the probability of the worst Phenotype 
 * individual to be  selected and <i>n</i><sub><i>p</i></sub>/<i>N</i> the probability 
 * of the best Phenotype individual to be selected. As the population size is 
 * held constant, the conditions <i>n</i><sub><i>p</i></sub> = 2 - <i>n</i><sub><i>m</i></sub>
 * and <i>n</i><sub><i>m</i></sub> >= 0 must be fulfilled. Note that all individuals 
 * get a different rank, i.e., a different selection probability, even if the 
 * have the same fitness value. <p/>
 * 
 * <i>
 * T. Blickle, L. Thiele, A comparison of selection schemes used 
 * in evolutionary algorithms, Technical Report, ETH Zurich, 1997, page 37.
 * <a href="http://citeseer.ist.psu.edu/blickle97comparison.html">
 * 	http://citeseer.ist.psu.edu/blickle97comparison.html
 * </a>
 * </i>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class LinearRankSelector<G extends Gene<?, G>, C extends Comparable<C>> 
	extends ProbabilitySelector<G, C> implements Serializable
{
	private static final long serialVersionUID = -7633503361447837511L;
	
	private final double _nminus;
	private final double _nplus;

	/**
	 * Create a new LinearRankSelector with {@code nminus := 0.5}.
	 */
	public LinearRankSelector() {
		this(0.5);
	}
	
	/**
	 * Create a new LinearRankSelector with the given values for {@code nminus}.
	 * 
	 * @param nminus {@code nminus/N} is the probability of the worst phenotype 
	 *        to be selected.
	 * @throws IllegalArgumentException if {@code nminus < 0}.
	 */
	public LinearRankSelector(final double nminus) {
		if (nminus < 0) {
			throw new IllegalArgumentException(String.format(
					"nminus is smaller than zero: %s", nminus
				));
		}
		
		_nminus = nminus;
		_nplus = 2 - _nminus;
	}

	@Override
	protected double[] probabilities(
		final Population<G, C> population, 
		final int count // not needed here
	) {
		assert(population != null);
		
		//Sort the population.
		population.sort();
		
		final double N = population.size();
		final double[] props = new double[population.size()];

		for (int i = 0, n = population.size(); i < n; ++i) {
			props[n - i - 1] = (_nminus + ((_nplus - _nminus)*i)/(N - 1))/N;
		}
		
		return props;
	}

}


