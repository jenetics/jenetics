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

import java.util.concurrent.atomic.AtomicInteger;

import org.jenetics.util.Array;
import org.jenetics.util.IndexStream;


/**
 * This class is for mutating a chromosomes of an given population. There are 
 * two distinct roles mutation plays
 * <ul>
 *	<li>Exploring the search space. By making small moves mutation allows a
 *	population to explore the search space. This exploration is often slow
 *	compared to crossover, but in problems where crossover is disruptive this
 *	can be an important way to explore the landscape.
 *	</li>
 *	<li>Maintaining diversity. Mutation prevents a population from
 *	correlating. Even if most of the search is being performed by crossover,
 *	mutation can be vital to provide the diversity which crossover needs.
 *	</li>
 * </ul>
 * 
 * The mutation probability is the parameter that must be optimized. The optimal 
 * value of the mutation rate depends on the role mutation plays. If mutation is 
 * the only source of exploration (if there is no crossover) then the mutation 
 * rate should be set so that a reasonable neighborhood of solutions is explored. 
 * </p>
 * The mutation probability <i>p</i> is the probability that a specific gene 
 * over the whole population is mutated. The number of available genes of an 
 * population is 
 * <p>
 * <img src="doc-files/mutator-N_G.gif" alt="N_G=n_P \cdot n_C \cdot n_G" />
 * </p>
 * where <i>n<sub>P</sub></i>  is the population size, <i>n<sub>C</sub></i> the 
 * number of chromosomes of the genotypes and <i>n<sub>G</sub></i> 
 * the number of genes in the chromosomes. So the (average) number of genes 
 * mutated by the mutation is 
 * <p>
 * <img src="doc-files/mutator-mean_m.gif" alt="\hat{\mu}_m=N_G \cdot p" />
 * </p>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Mutator<G extends Gene<?, G>> extends AbstractAlterer<G> {	
	
	/**
	 * Default constructor, with probability = 0.01.
	 */
	public Mutator() {
		this(0.01);
	}
	
	/**
	 * Construct a Mutation object which a given mutation probability.
	 * 
	 * @param probability Mutation probability. The given probability is
	 *			divided by the number of chromosomes of the genotype to form
	 *			the concrete mutation probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 * 		  valid range of {@code [0, 1]}..
	 */
	public Mutator(final double probability) {
		super(probability);
	}
 
	/**
	 * Concrete implementation of the alter method.
	 */
	@Override
	public <C extends Comparable<? super C>> int alter(
		final Population<G, C> population, 
		final int generation
	) {
		assert(population != null) : "Not null is guaranteed from base class.";
		
		final double p = Math.pow(_probability, 1.0/3.0);
		final AtomicInteger alterations = new AtomicInteger(0);
		
		final IndexStream stream = randomIndexes(population.size(), p); 		
		for (int i = stream.next(); i != -1; i = stream.next()) {
			final Phenotype<G, C> pt = population.get(i);
			
			final Genotype<G> gt = pt.getGenotype(); 
			final Genotype<G> mgt = mutate(gt, p, alterations);
			
			final Phenotype<G, C> mpt = pt.newInstance(mgt, generation);
			population.set(i, mpt);
		}
		
		return alterations.get();
	}
	
	private Genotype<G> mutate(
		final Genotype<G> genotype, 
		final double p,
		final AtomicInteger alterations
	) {
		Genotype<G> gt = genotype;

		final IndexStream stream = randomIndexes(genotype.length(), p);
		int start = stream.next();
		if (start != -1) {
			final Array<Chromosome<G>> chromosomes = genotype.getChromosomes().copy(); 
			
			for (int i = start; i != -1; i = stream.next()) {
				final Chromosome<G> chromosome = chromosomes.get(i);
				final Array<G> genes = chromosome.toArray().copy();
				
				final int mutations = mutate(genes, p);
				if (mutations > 0) {
					alterations.addAndGet(mutations);
					chromosomes.set(i, chromosome.newInstance(genes));
				}
			}
				
			gt = genotype.newInstance(chromosomes);
		}
		
		return gt;
	}
	
	/**
	 * Template method which gives an (re)implementation of the mutation class the
	 * possibility to perform its own mutation operation, based on a writable
	 * gene array and the gene mutation probability <i>p</i>. 
	 * <p/>
	 * This implementation, for example, does it in this way:
	 * [code]
	 *     protected int mutate(final Array<G> genes, final double p) {
	 *         final Random random = RandomRegistry.getRandom();
	 *         final ProbabilityIndexIterator it = 
	 *             new ProbabilityIndexIterator(genes.length(), p, random);
	 *	
	 *         int alterations = 0;
	 *         for (int i = it.next(); i != -1; i = it.next()) {
	 *             genes.set(i, genes.get(i).newInstance());
	 *             ++alterations;
	 *         }
	 *         return alterations;
	 *     }
	 * [/code]
	 * 
	 * @param genes the genes to mutate.
	 * @param p the gene mutation probability.
	 */
	protected int mutate(final Array<G> genes, final double p) {
		final IndexStream stream = randomIndexes(genes.length(), p);
		
		int alterations = 0;
		for (int i = stream.next(); i != -1; i = stream.next()) {
			genes.set(i, genes.get(i).newInstance());
			++alterations;
		}
		
		return alterations;
	}
	
	@Override
	public int hashCode() {
		int hash = 461;
		hash += 61*super.hashCode() + 17;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof Mutator<?>;
	}
	
	@Override
	public String toString() {
		return String.format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}




