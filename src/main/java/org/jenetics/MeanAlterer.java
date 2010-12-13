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
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;


/**
 * <p>
 * The order ({@link #getOrder()}) of this Recombination implementation is two.
 * </p>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class MeanAlterer<G extends Gene<?, G> & Mean<G>> 
	extends Recombination<G> 
{

	public MeanAlterer() {
		this(0.05);
	}
	
	/**
	 * Constructs an alterer with a given recombination probability.
	 * 
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 * 		  valid range of {@code [0, 1]}.
	 */
	public MeanAlterer(final double probability) {
		super(probability, 2);
	}

	@Override
	protected <C extends Comparable<? super C>> int recombinate(
		final Population<G, C> population, 
		final int[] individuals, 
		final int generation
	) {
		final Random random = RandomRegistry.getRandom();
		
		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();
		
		final int cindex = random.nextInt(gt1.length());
		final Array<Chromosome<G>> c1 = gt1.getChromosomes();
		final Array<Chromosome<G>> c2 = gt2.getChromosomes();
		
		// Calculate the mean value of the gene array.
		final Array<G> mean = mean(
				c1.get(cindex).toArray().copy(), 
				c2.get(cindex).toArray()
			);
		
		c1.set(cindex, c1.get(cindex).newInstance(mean));
		
		population.set(
				individuals[0], 
				pt1.newInstance(Genotype.valueOf(c1), generation)
			);
		
		return 1;
	}
	
	private Array<G> mean(final Array<G> a, final Array<G> b) {
		for (int i = a.length(); --i >= 0;) {
			a.set(i, a.get(i).mean(b.get(i)));
		}
		return a;
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
		if (!(obj instanceof MeanAlterer<?>)) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return String.format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}


