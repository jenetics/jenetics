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
import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Probability;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: SwapMutation.java,v 1.1 2009-02-24 21:25:44 fwilhelm Exp $
 */
public class SwapMutation<G extends Gene<?, G>> extends Mutation<G> {

	public SwapMutation() {
	}

	public SwapMutation(final Probability probability, final Alterer<G> component) {
		super(probability, component);
	}

	public SwapMutation(final Probability probability) {
		super(probability);
	}

	@Override
	protected void mutate(final Array<G> genes) {
		final Random random = RandomRegistry.getRandom();
		final int subsetSize = (int)Math.ceil(genes.length()*_probability.doubleValue());
		final int[] elements = subset(genes.length(), subsetSize, random);
				
		for (int i = 0; i < elements.length; ++i) {
			ArrayUtils.swap(genes, elements[i], random.nextInt(genes.length()));
		}
		
		_mutations += elements.length;
	}

	
	
}
