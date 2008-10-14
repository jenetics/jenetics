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
 * @param <T> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Crossover.java,v 1.10 2008-10-14 21:10:04 fwilhelm Exp $
 */
public abstract class Crossover<T extends Gene<?>> extends Alterer<T> {
	private static final long serialVersionUID = 6083622511856683392L;

	public Crossover(final Alterer<T> component) {
		super(component);
	}
	
	public Crossover(final Probability probability, final Alterer<T> component) {
		super(probability, component);
	}

	public Crossover(final Probability probability) {
		super(probability);
	}

	@Override
	protected <C extends Comparable<C>> void componentAlter(
		final Population<T, C> population, final int generation
	) {
		assert(population != null) : "Not null is guaranteed from base class.";
		
		final Random random = RandomRegistry.getRandom();
		for (int i = population.size(); --i >= 0;) { 
			//Performing the crossover with the given probability.
			if (!_probability.isLargerThan(random.nextDouble())) {
				final int ptIndex = random.nextInt(population.size());
				final Phenotype<T, C> pt1 = population.get(i);
				final Phenotype<T, C> pt2 = population.get(ptIndex);
				final Genotype<T> gt1 = pt1.getGenotype();
				final Genotype<T> gt2 = pt2.getGenotype();
				
				//Choosing two Chromosome for crossover randomly.
				final int chIndex1 = random.nextInt(gt1.chromosomes());
				final int chIndex2 = random.nextInt(gt2.chromosomes());
				
				final Array<Chromosome<T>> chromosomes1 = gt1.getChromosomes();
				final Array<Chromosome<T>> chromosomes2 = gt2.getChromosomes();
				final Array<T> genes1 = chromosomes1.get(chIndex1).getGenes().copy();
				final Array<T> genes2 = chromosomes2.get(chIndex2).getGenes().copy();
				
				crossover(genes1, genes2);
				
				chromosomes1.set(chIndex1, chromosomes1.get(chIndex1).newChromosome(genes1));
				chromosomes2.set(chIndex2, chromosomes2.get(chIndex2).newChromosome(genes2));
				
				//Creating two new Phenotypes and exchanging it with the old.
				population.set(i, pt1.newInstance(Genotype.valueOf(chromosomes1), generation));
				population.set(ptIndex, pt2.newInstance(Genotype.valueOf(chromosomes2), generation));
			}
		}
	}

	/**
	 * Template method which performs the crossover.
	 */
	protected abstract void crossover(final Array<T> that, final Array<T> other);
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




