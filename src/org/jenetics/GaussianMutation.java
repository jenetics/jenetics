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

import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: GaussianMutation.java,v 1.1 2009-02-22 23:29:58 fwilhelm Exp $
 */
public class GaussianMutation<G extends NumberGene<?>> extends Mutation<G> {
	private final double _variance;
	
	public GaussianMutation() {
		this(1);
	}
	
	public GaussianMutation(final double variance) {
		_variance = variance;
	}
	
	//TODO: implement
	@Override
	public Chromosome<G> mutate(final Chromosome<G> chromosome) {
		final Random random = RandomRegistry.getRandom();
		final int index = random.nextInt(chromosome.length());
		
		final Array<G> genes = chromosome.toArray().copy();
		final G oldGene = genes.get(index);
		
		double value = oldGene.doubleValue()*_variance;
		
		@SuppressWarnings("unchecked")
		final G newGene = (G) oldGene.newInstance(value);
		
		genes.set(index, newGene);
		
		return chromosome;
	}
	
}





