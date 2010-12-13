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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class CompositeAltererTest {

	public Alterer<Float64Gene> newAlterer(double p) {
		p = Math.pow(p, 3);
		return new CompositeAlterer<Float64Gene>(
					new Mutator<Float64Gene>(p),
					new Mutator<Float64Gene>(p),
					new Mutator<Float64Gene>(p)
				);
	}
	
	@Test(dataProvider = "alterCountParameters") 
	public void alterCount(
		final Integer ngenes, 
		final Integer nchromosomes, 
		final Integer npopulation
	) {
		final Population<Float64Gene, Float64> p1 = population(
					ngenes, nchromosomes, npopulation
				);
		final Population<Float64Gene, Float64> p2 = p1.copy();
		Assert.assertEquals(p2, p1);
		
		final Alterer<Float64Gene> mutator = newAlterer(0.01);
		
		Assert.assertEquals(mutator.alter(p1, 1), diff(p1, p2));
	}
	
	public final Population<Float64Gene, Float64> population(
			final int ngenes, 
			final int nchromosomes, 
			final int npopulation
		) {
			final Array<Float64Chromosome> chromosomes = 
				new Array<Float64Chromosome>(nchromosomes);
			
			for (int i = 0; i < nchromosomes; ++i) {
				chromosomes.set(i, new Float64Chromosome(0, 10, ngenes));
			}	
			
			final Genotype<Float64Gene> genotype = Genotype.valueOf(chromosomes);
			final Population<Float64Gene, Float64> population = 
				new Population<Float64Gene, Float64>(npopulation);
			
			for (int i = 0; i < npopulation; ++i) {
				population.add(Phenotype.valueOf(genotype.newInstance(), TestUtils.FF, 0));
			}	
			
			return population;
		}
	
	/*
	 * Count the number of different genes.
	 */
	public int diff(
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
	
	@DataProvider(name = "alterCountParameters")
	public Object[][] alterCountParameters() {
		return new Object[][] {
				//    ngenes,       nchromosomes     npopulation
				{ new Integer(1),   new Integer(1),  new Integer(100) },
				{ new Integer(5),   new Integer(1),  new Integer(100) },
				{ new Integer(80),  new Integer(1),  new Integer(100) },
				{ new Integer(1),   new Integer(2),  new Integer(100) },
				{ new Integer(5),   new Integer(2),  new Integer(100) },
				{ new Integer(80),  new Integer(2),  new Integer(100) },
				{ new Integer(1),   new Integer(15), new Integer(100) },
				{ new Integer(5),   new Integer(15), new Integer(100) },
				{ new Integer(80),  new Integer(15), new Integer(100) },
				
				{ new Integer(1),   new Integer(1),  new Integer(150) },
				{ new Integer(5),   new Integer(1),  new Integer(150) },
				{ new Integer(80),  new Integer(1),  new Integer(150) },
				{ new Integer(1),   new Integer(2),  new Integer(150) },
				{ new Integer(5),   new Integer(2),  new Integer(150) },
				{ new Integer(80),  new Integer(2),  new Integer(150) },
				{ new Integer(1),   new Integer(15), new Integer(150) },
				{ new Integer(5),   new Integer(15), new Integer(150) },
				{ new Integer(80),  new Integer(15), new Integer(150) },
				
				{ new Integer(1),   new Integer(1),  new Integer(500) },
				{ new Integer(5),   new Integer(1),  new Integer(500) },
				{ new Integer(80),  new Integer(1),  new Integer(500) },
				{ new Integer(1),   new Integer(2),  new Integer(500) },
				{ new Integer(5),   new Integer(2),  new Integer(500) },
				{ new Integer(80),  new Integer(2),  new Integer(500) },
				{ new Integer(1),   new Integer(15), new Integer(500) },
				{ new Integer(5),   new Integer(15), new Integer(500) },
				{ new Integer(80),  new Integer(15), new Integer(500) }
		};
	}
	@Test
	public void join() {
		CompositeAlterer<Float64Gene> alterer = CompositeAlterer.join(
				new Mutator<Float64Gene>(),
				new GaussianMutator<Float64Gene>()
			);
		
		Assert.assertEquals(alterer.getAlterers().length(), 2);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new GaussianMutator<Float64Gene>());
		
		alterer = CompositeAlterer.join(alterer, new MeanAlterer<Float64Gene>());
		
		Assert.assertEquals(alterer.getAlterers().length(), 3);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new GaussianMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new MeanAlterer<Float64Gene>());
		
		alterer = new CompositeAlterer<Float64Gene>(
				new MeanAlterer<Float64Gene>(),
				new SwapMutator<Float64Gene>(),
				alterer,
				new SwapMutator<Float64Gene>()
			);
		
		Assert.assertEquals(alterer.getAlterers().length(), 6);
		Assert.assertEquals(alterer.getAlterers().get(0), new MeanAlterer<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new SwapMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(3), new GaussianMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(4), new MeanAlterer<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(5), new SwapMutator<Float64Gene>());
	}
	
}
