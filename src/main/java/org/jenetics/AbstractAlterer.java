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

import static org.jenetics.util.Validator.checkProbability;

import org.jenetics.util.ProbabilityIndexIterator;
import org.jenetics.util.RandomRegistry;

/**
 * Abstract implementation of the alterer interface.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class AbstractAlterer<G extends Gene<?, G>> 
	implements Alterer<G> 
{
	public static final double DEFAULT_ALTER_PROBABILITY = 0.2;
	
	/**
	 * The altering probability. 
	 */
	protected final double _probability;
	
	/**
	 * Constructs an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 * 		  valid range of {@code [0, 1]}.
	 */
	protected AbstractAlterer(final double probability) {
		_probability = checkProbability(probability);
	}
	
	/**
	 * Return the recombination/alter probability for this alterer.
	 * 
	 * @return The recombination probability.
	 */
	public double getProbability() {
		return _probability;
	}
	
	/**
	 * Helper method which creates an index iterator with the given maximum
	 * length and probability.
	 * 
	 * @param length the maximal index (exclusively) of the iterator.
	 * @param p the index selection probability.
	 * @return new index iterator.
	 */
	protected static ProbabilityIndexIterator iterator(final int length, final double p) {
		return new ProbabilityIndexIterator(length, p, RandomRegistry.getRandom());
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += 37*Double.doubleToLongBits(_probability) + 17;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AbstractAlterer<?>)) {
			return false;
		}
		
		final AbstractAlterer<?> alterer = (AbstractAlterer<?>)obj;
		return Double.doubleToLongBits(_probability) == 
				Double.doubleToLongBits(alterer._probability);
	}
}




