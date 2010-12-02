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

import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import org.jenetics.stat.Distribution.Domain;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Array;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class MutatorTestBase {
	
	public abstract Alterer<Float64Gene> newAlterer(final double p);
	
	
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
	
	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes, 
		final Integer nchromosomes, 
		final Integer npopulation,
		final Double p
	) {		
		final Population<Float64Gene, Float64> population = population(
				ngenes, nchromosomes, npopulation
			);
		
		// The mutator to test.
		final Alterer<Float64Gene> mutator = newAlterer(p);
		
		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 100;
		final double mean = nallgenes*p;
		
		final long min = 0;
		final long max = nallgenes;
		final Domain<Long> domain = new Domain<Long>(min, max);
		
		final Histogram<Long> histogram = Histogram.valueOf(min, max, 10);	
		final Variance<Long> variance = new Variance<Long>();
		
		for (int i = 0; i < N; ++i) {
			final long alterations = mutator.alter(population, 1);
			histogram.accumulate(alterations);
			variance.accumulate(alterations);	
		}
				
		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<Long>(domain, mean, variance.getVariance()));
	}
	
	
	// Create a random population of Float64Genes.
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
			population.add(Phenotype.valueOf(genotype.newInstance(), Factories.FF, 0));
		}	
		
		return population;
	}
	
	public double var(final double p, final long N) {
		return N*p*(1.0 - p);
	}
	
	public double mean(final double p, final long N) {
		return N*p;
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
	
	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return new Object[][] {
				//    ngenes,       nchromosomes     npopulation
				{ new Integer(20),   new Integer(20),  new Integer(20), new Double(0.5) },
				{ new Integer(1),   new Integer(1),  new Integer(150), new Double(0.15) },
				{ new Integer(5),   new Integer(1),  new Integer(150), new Double(0.15) },
				{ new Integer(80),  new Integer(1),  new Integer(150), new Double(0.15) },
				{ new Integer(1),   new Integer(2),  new Integer(150), new Double(0.15) },
				{ new Integer(5),   new Integer(2),  new Integer(150), new Double(0.15) },
				{ new Integer(80),  new Integer(2),  new Integer(150), new Double(0.15) },
				{ new Integer(1),   new Integer(15), new Integer(150), new Double(0.15) },
				{ new Integer(5),   new Integer(15), new Integer(150), new Double(0.15) },
				{ new Integer(80),  new Integer(15), new Integer(150), new Double(0.15) },
				
				{ new Integer(1),   new Integer(1),  new Integer(150), new Double(0.5) },
				{ new Integer(5),   new Integer(1),  new Integer(150), new Double(0.5) },
				{ new Integer(80),  new Integer(1),  new Integer(150), new Double(0.5) },
				{ new Integer(1),   new Integer(2),  new Integer(150), new Double(0.5) },
				{ new Integer(5),   new Integer(2),  new Integer(150), new Double(0.5) },
				{ new Integer(80),  new Integer(2),  new Integer(150), new Double(0.5) },
				{ new Integer(1),   new Integer(15), new Integer(150), new Double(0.5) },
				{ new Integer(5),   new Integer(15), new Integer(150), new Double(0.5) },
				{ new Integer(80),  new Integer(15), new Integer(150), new Double(0.5) },				
				
				{ new Integer(1),   new Integer(1),  new Integer(150), new Double(0.85) },
				{ new Integer(5),   new Integer(1),  new Integer(150), new Double(0.85) },
				{ new Integer(80),  new Integer(1),  new Integer(150), new Double(0.85) },
				{ new Integer(1),   new Integer(2),  new Integer(150), new Double(0.85) },
				{ new Integer(5),   new Integer(2),  new Integer(150), new Double(0.85) },
				{ new Integer(80),  new Integer(2),  new Integer(150), new Double(0.85) },
				{ new Integer(1),   new Integer(15), new Integer(150), new Double(0.85) },
				{ new Integer(5),   new Integer(15), new Integer(150), new Double(0.85) },
				{ new Integer(80),  new Integer(15), new Integer(150), new Double(0.85) }
		};
	}
	
}
