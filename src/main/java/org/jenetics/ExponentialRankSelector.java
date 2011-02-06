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

import static java.lang.Math.pow;
import static org.jenetics.util.ObjectUtils.eq;
import static org.jenetics.util.ObjectUtils.hashCodeOf;

import javolution.lang.Immutable;

/**
 * <p>
 * An alternative to the "weak" <code>LinearRankSelector</code> is to assign
 * survival probabilities to the sorted individuals using an exponential 
 * function.
 * </p>
 * <p/><img 
 *        src="doc-files/exponential-rank-selector.gif" 
 *        alt="p_i=(c-1)\frac{c^{i-1}}{c^N-1}" 
 *     />,
 * </p>
 * where <i>c</i> must within the range {@code [0..1)}.
 * 
 * <p>
 * A small value of <i>c</i> increases the probability of the best phenotypes to
 * be selected. If <i>c</i> is set to zero, the selection probability of the best
 * phenotype is set to one. The selection probability of all other phenotypes is
 * zero. A value near one equalizes the selection probabilities. 
 * </p>
 * <p>
 * This selector sorts the population in descending order while calculating the
 * selection probabilities.
 * </p>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class ExponentialRankSelector<
	G extends Gene<?, G>, 
	C extends Comparable<? super C>
> 
	extends ProbabilitySelector<G, C>
	implements Immutable
{
	
	private final double _c;
	
	/**
	 * Create a new exponential rank selector.
	 * 
	 * @param c the <i>c</i> value.
	 * @throws IllegalArgumentException if {@code c} is not within the range
	 * 		  {@code [0..1)}.
	 */
	public ExponentialRankSelector(final double c) {
		if (c < 0.0 || c >= 1.0) {
			throw new IllegalArgumentException(String.format(
					"Value is out of range [0..1): ", c
				));
		}
		_c = c;
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#sort()} is called
	 * by this method.)
	 */
	@Override
	protected double[] probabilities(
		final Population<G, C> population, 
		final int count
	) {
		assert(population != null) : "Population can not be null. ";
		assert(count >= 0) : "Population to select must be greater than zero. ";
		
		//Sorted population required.
		population.sort();
		
		final double N = population.size();
		final double[] probabilities = new double[population.size()];
		
		final double b = pow(_c, N) - 1;
		for (int i = probabilities.length; --i >= 0;) {
			probabilities[i] = ((_c - 1)*pow(_c, i))/b;
		}
	
		assert (sum2one(probabilities)) : "Probabilities doesn't sum to one.";
		return probabilities;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(super.hashCode()).and(_c).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		
		final ExponentialRankSelector<?, ?> selector = (ExponentialRankSelector<?, ?>)obj;
		return eq(_c, selector._c) && super.equals(selector);
	}
	
	@Override
	public String toString() {
		return String.format("%s[c=%f]", getClass().getSimpleName(), _c);
	}

}



