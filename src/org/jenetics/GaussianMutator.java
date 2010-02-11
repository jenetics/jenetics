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

import static org.jenetics.util.ArrayUtils.subset;

import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.RandomRegistry;

/**
 * The GaussianRealMutator class performs the mutation of a {@link NumberGene}. 
 * This mutator picks a new value based on a Gaussian distribution (with 
 * deviation 1.0)  around the current value of the gene. The new value won't be 
 * out of the gene's boundaries.
 *
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class GaussianMutator<G extends NumberGene<?, G>> extends Mutator<G> {
	
	public GaussianMutator() {
	}

	public GaussianMutator(final double probability) {
		super(probability);
	}

	@Override
	protected void mutate(final Array<G> genes) {
		final Random random = RandomRegistry.getRandom();
		final int subsetSize = (int)Math.ceil(genes.length()*_probability);
		
		if (subsetSize > 0) {
			final int[] elements = subset(genes.length(), subsetSize, random);

			for (int i = 0; i < elements.length; ++i) {
				final G oldGene = genes.get(elements[i]);
				double value = random.nextGaussian()*oldGene.doubleValue();
				value = Math.min(value, oldGene.getMax().doubleValue());
				value = Math.max(value, oldGene.getMin().doubleValue());

				final G newGene = oldGene.newInstance(value);
				genes.set(elements[i], newGene);

			}
			
			//Count the number of mutated genes.
			_mutations += elements.length;
		}
	}
	
}





