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

import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.ProbabilityIndexIterator;
import org.jenetics.util.RandomRegistry;

/**
 * The GaussianRealMutator class performs the mutation of a {@link NumberGene}. 
 * This mutator picks a new value based on a Gaussian distribution (with 
 * deviation 1.0)  around the current value of the gene. The new value won't be 
 * out of the gene's boundaries.
 *
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class GaussianMutator<G extends NumberGene<?, G>> extends Mutator<G> {
	
	public GaussianMutator() {
	}

	public GaussianMutator(final double probability) {
		super(probability);
	}

	@Override
	protected int mutate(final Array<G> genes, final double p) {
		final Random random = RandomRegistry.getRandom();
		final ProbabilityIndexIterator it = iterator(genes.length(), p);
		
		int alterations = 0;
		for (int i = it.next(); i != -1; i = it.next()) {
			final G og = genes.get(i);
			double value = random.nextGaussian()*og.doubleValue();
			value = Math.min(value, og.getMax().doubleValue());
			value = Math.max(value, og.getMin().doubleValue());

			genes.set(i, og.newInstance(value));
			
			++_mutations;
			++alterations;
		}
		
		return alterations;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += 17*super.hashCode() + 37;
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





