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

import static org.jenetics.util.Validator.notNull;

import org.jenetics.util.Probability;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: AbstractAlterer.java,v 1.1 2010-01-27 20:35:44 fwilhelm Exp $
 */
public abstract class AbstractAlterer<G extends Gene<?, G>> implements Alterer<G> {
	public static final Probability DEFAULT_ALTER_PROBABILITY = Probability.valueOf(0.2);
	
	/**
	 * The altering probability. 
	 */
	protected final Probability _probability;
	
	/**
	 * Constructs an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws NullPointerException if the {@code probability} is {@code null}. 
	 */
	protected AbstractAlterer(final Probability probability) {
		_probability = notNull(probability, "Probability");
	}
	
	/**
	 * Return the recombination probability for this alterer.
	 * 
	 * @return The recombination probability.
	 */
	public Probability getProbability() {
		return _probability;
	}
}
