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
import org.jenetics.util.RandomRegistry;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: MeanAlterer.java,v 1.10 2008-11-13 20:37:40 fwilhelm Exp $
 */
public class MeanAlterer<G extends Gene<?> & Mean<G>> extends Recombination<G> {

	public MeanAlterer() {
		this(Probability.valueOf(0.05));
	}
	
	public MeanAlterer(final Probability probability) {
		super(probability);
	}

	public MeanAlterer(final Probability probability, final Alterer<G> component) {
		super(probability, component);
	}

	@Override
	protected <C extends Comparable<C>> void recombinate(
		final Population<G, C> population, 
		final int first, final int second, final int generation
	) {
		final Random random = RandomRegistry.getRandom();
		
		final Phenotype<G, C> pt1 = population.get(first);
		final Phenotype<G, C> pt2 = population.get(second);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();
		
		final int chIndex = random.nextInt(gt1.chromosomes());
		final Array<Chromosome<G>> chromosomes1 = gt1.getChromosomes();
		final Array<Chromosome<G>> chromosomes2 = gt2.getChromosomes();
		final Array<G> genes1 = chromosomes1.get(chIndex).getGenes().copy();
		final Array<G> genes2 = chromosomes2.get(chIndex).getGenes().copy();
		
		final int geneIndex = random.nextInt(genes1.length());
		
		genes1.set(geneIndex, genes1.get(geneIndex).mean(genes2.get(geneIndex)));
		genes2.set(geneIndex, genes1.get(geneIndex));
		chromosomes1.set(chIndex, chromosomes1.get(chIndex).newChromosome(genes1));
		chromosomes2.set(chIndex, chromosomes2.get(chIndex).newChromosome(genes2));
		
		population.set(first, pt1.newInstance(Genotype.valueOf(chromosomes1), generation));
		population.set(second, pt2.newInstance(Genotype.valueOf(chromosomes2), generation));
	}

}


