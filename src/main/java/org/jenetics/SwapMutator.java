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

import static org.jenetics.util.ArrayUtils.subset;

import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.ArrayUtils;
import org.jenetics.util.RandomRegistry;

/**
 * The {@code SwapMutation} changes the order of genes in a chromosome, with the 
 * hope of bringing related genes closer together, thereby facilitating the 
 * production of building blocks. This mutation operator can also be used for
 * combinatorial problems, where no duplicated genes within a chromosome are
 * allowed, e.g. for the TSP.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class SwapMutator<G extends Gene<?, G>> extends Mutator<G> {

	/**
	 * Default constructor, with default mutation probability 
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public SwapMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Constructs an alterer with a given recombination probability.
	 * 
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 * 		  valid range of {@code [0, 1]}.
	 */
	public SwapMutator(final double probability) {
		super(probability);
	}

	/**
	 * Swaps the genes in the given array, with the mutation probability of this
	 * mutation.
	 */
	@Override
	protected int mutate(final Array<G> genes, final double probability) {
		final Random random = RandomRegistry.getRandom();
		final int subsetSize = (int)Math.ceil(genes.length()*_probability);
		
		int alterations = 0;
		if (subsetSize > 0) {
			final int[] elements = subset(genes.length(), subsetSize, random);
					
			for (int i = 0; i < elements.length; ++i) {
				ArrayUtils.swap(genes, elements[i], random.nextInt(genes.length()));
			}
			
			_mutations += elements.length;
			alterations = elements.length;
		}
		
		return alterations;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 37*super.hashCode() + 61;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return String.format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}
	
}
