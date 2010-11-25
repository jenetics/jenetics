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
 * This mutator picks a new value based on a Gaussian distribution around the 
 * current value of the gene. The variance of the new value (before clipping to
 * the allowed gene range) will be 
 * <p>
 * <img 
 *     src="doc-files/gaussian-mutator-var.gif" 
 *     alt="\hat{\sigma }^2 = \left ( \frac{ G_{max} - G_{min} }{4}\right )^2" 
 * />
 * </p>
 * The new value will be cropped to the gene's boundaries.
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
			final G g = genes.get(i);
			final double std = (g.getMax().doubleValue() - g.getMin().doubleValue())/4.0;
			
			double value = random.nextGaussian()*std + g.doubleValue();
			value = Math.min(value, g.getMax().doubleValue());
			value = Math.max(value, g.getMin().doubleValue());

			genes.set(i, g.newInstance(value));
			
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





