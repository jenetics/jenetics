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
import org.jenetics.util.RandomRegistry;

/**
 * Performs a <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29">
 * Crossover</a> of two {@link Chromosome}.
 * 
 * @param <G> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class Crossover<G extends Gene<?, G>> extends Recombination<G> {

	/**
	 * Constructs an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	public Crossover(final double probability) {
		super(probability);
	}
	
	@Override
	protected final <C extends Comparable<? super C>> void recombinate(
		final Population<G, C> population, 
		final int first, final int second, final int generation
	) {
		final Random random = RandomRegistry.getRandom();
		
		final Phenotype<G, C> pt1 = population.get(first);
		final Phenotype<G, C> pt2 = population.get(second);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();
		
		//Choosing the Chromosome for crossover.
		final int chIndex = random.nextInt(gt1.length());
		
		final Array<Chromosome<G>> chromosomes1 = gt1.getChromosomes();
		final Array<Chromosome<G>> chromosomes2 = gt2.getChromosomes();
		final Array<G> genes1 = chromosomes1.get(chIndex).toArray().copy();
		final Array<G> genes2 = chromosomes2.get(chIndex).toArray().copy();
		
		crossover(genes1, genes2);
		
		chromosomes1.set(chIndex, chromosomes1.get(chIndex).newInstance(genes1));
		chromosomes2.set(chIndex, chromosomes2.get(chIndex).newInstance(genes2));
		
		//Creating two new Phenotypes and exchanging it with the old.
		population.set(first, pt1.newInstance(Genotype.valueOf(chromosomes1), generation));
		population.set(second, pt2.newInstance(Genotype.valueOf(chromosomes2), generation));
	}

	/**
	 * Template method which performs the crossover. The arguments given are 
	 * mutable non null arrays of the same length.
	 */
	protected abstract void crossover(final Array<G> that, final Array<G> other);

}




