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

import org.jenetics.util.Array;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class MutatorTest {	
	
	
	@Test
	public void mutate() {
		final int npop = 1000;
		final int ngene = 100;
		final int nchrom = 10; 
		
		
		final Array<Float64Chromosome> chromosomes = new Array<Float64Chromosome>(nchrom);
		for (int i = 0; i < nchrom; ++i) {
			chromosomes.set(i, new Float64Chromosome(0, 10, ngene));
		}		
		final Genotype<Float64Gene> genotype = Genotype.valueOf(chromosomes);
		
		
		final Population<Float64Gene, Float64> pop1 = new Population<Float64Gene, Float64>(npop);
		for (int i = 0; i < npop; ++i) {
			pop1.add(Phenotype.valueOf(genotype.newInstance(), Factories.FF, 0));
		}
		final Population<Float64Gene, Float64> pop2 = pop1.copy();
		
		final Mutator<Float64Gene> mutator = new Mutator<Float64Gene>(0.01);
		
		Assert.assertEquals(mutator.alter(pop1, 1), diff(pop1, pop2));
		System.out.println(mutator.alter(pop1, 1));
		System.out.println(genotype.getNumberOfGenes());
	}
	
	private int diff(
		final Population<Float64Gene, Float64> p1, 
		final Population<Float64Gene, Float64> p2
	) {
		int count = 0;
		for (int i = 0; i < p1.size(); ++i) {
			final Genotype<?> gt1 = p1.get(i).getGenotype();
			final Genotype<?> gt2 = p2.get(i).getGenotype();
			
			for (int j = 0; j < gt1.length(); ++j) {
				final Chromosome<?> c1 = gt1.getChromosome(j);
				final Chromosome<?> c2 = gt2.getChromosome(j);
				
				for (int k = 0; k < c1.length(); ++k) {
					if (!c1.getGene(k).equals(c2.getGene(k))) {
						++count;
					}
				}
			}
		}
		return count;
	}
	
}
