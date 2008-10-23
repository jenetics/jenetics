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
import org.jenetics.util.Probability;

/**
 * Performs a <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29">
 * Crossover</a> of two {@link Chromosome}.
 * 
 * @param <G> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Crossover.java,v 1.12 2008-10-23 22:46:06 fwilhelm Exp $
 */
public abstract class Crossover<G extends Gene<?>> extends Recombination<G> {

	public Crossover(final Alterer<G> component) {
		super(component);
	}
	
	public Crossover(final Probability probability, final Alterer<G> component) {
		super(probability, component);
	}

	public Crossover(final Probability probability) {
		super(probability);
	}
	
	@Override
	protected final <C extends Comparable<C>> void recombinate(
		Population<G, C> population, int first, int second, int generation
	) {
		final Random random = RandomRegistry.getRandom();
		
		final Phenotype<G, C> pt1 = population.get(first);
		final Phenotype<G, C> pt2 = population.get(second);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();
		
		//Choosing two Chromosome for crossover randomly.
		final int chIndex1 = random.nextInt(gt1.chromosomes());
		final int chIndex2 = random.nextInt(gt2.chromosomes());
		
		final Array<Chromosome<G>> chromosomes1 = gt1.getChromosomes();
		final Array<Chromosome<G>> chromosomes2 = gt2.getChromosomes();
		final Array<G> genes1 = chromosomes1.get(chIndex1).getGenes().copy();
		final Array<G> genes2 = chromosomes2.get(chIndex2).getGenes().copy();
		
		crossover(genes1, genes2);
		
		chromosomes1.set(chIndex1, chromosomes1.get(chIndex1).newChromosome(genes1));
		chromosomes2.set(chIndex2, chromosomes2.get(chIndex2).newChromosome(genes2));
		
		//Creating two new Phenotypes and exchanging it with the old.
		population.set(first, pt1.newInstance(Genotype.valueOf(chromosomes1), generation));
		population.set(second, pt2.newInstance(Genotype.valueOf(chromosomes2), generation));
	}

	/**
	 * Template method which performs the crossover.
	 */
	protected abstract void crossover(final Array<G> that, final Array<G> other);
//	protected void crossover(Chromosome<T> that, Chromosome<T> other) {
//		final Random random = RandomRegistry.getRandom();
//		int from = random.nextInt(that.length());
//		int to = random.nextInt(other.length());
//		from = min(from, to);
//		to = max(from, to) + 1;
//		
//		for (int i = from; i < to; ++i) {
//			T temp = that.getGene(i);
//			that.setGene(i, other.getGene(i));
//			other.setGene(i, temp);
//		}
//	}
}




