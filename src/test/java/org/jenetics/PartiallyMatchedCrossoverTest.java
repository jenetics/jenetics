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

import static org.jenetics.TestUtils.newPermutationFloat64GenePopulation;
import static org.jenetics.stat.StatisticsAssert.assertDistribution;
import static org.jenetics.util.factories.Int;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Array;
import org.jenetics.util.ISeq;
import org.jenetics.util.Range;
import org.jenetics.util.arrays;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PartiallyMatchedCrossoverTest {

	
	@Test(invocationCount = 10)
	public void crossover() {
		final PartiallyMatchedCrossover<Integer> pmco = 
			new PartiallyMatchedCrossover<>(1);
		
		final int length = 1000;
		final Array<Integer> alleles = new Array<Integer>(length).fill(Int());
		final ISeq<Integer> ialleles = alleles.toISeq();
		
		final Array<PermutationGene<Integer>> that = alleles.map(PermutationGene.ToGene(ialleles));
		final Array<PermutationGene<Integer>> other = alleles.map(PermutationGene.ToGene(ialleles));
		
		arrays.shuffle(that);
		arrays.shuffle(other);
		
		final PermutationChromosome<Integer> thatChrom1 = PermutationChromosome.valueOf(that.toISeq());
		Assert.assertTrue(thatChrom1.isValid(), "thatChrom1 not valid");
		
		final PermutationChromosome<Integer> otherChrom1 = PermutationChromosome.valueOf(other.toISeq());
		Assert.assertTrue(otherChrom1.isValid(), "otherChrom1 not valid");
		
		pmco.crossover(that, other);
		
		final PermutationChromosome<Integer> thatChrom2 = PermutationChromosome.valueOf(that.toISeq());
		Assert.assertTrue(thatChrom2.isValid(), "thatChrom2 not valid: " + thatChrom2.toSeq());
		
		final PermutationChromosome<Integer> otherChrom2 = PermutationChromosome.valueOf(other.toISeq());
		Assert.assertTrue(otherChrom2.isValid(), "otherChrom2 not valid: " + otherChrom2.toSeq());
		
		Assert.assertFalse(thatChrom1.equals(thatChrom2), "That chromosome must not be equal");
		Assert.assertFalse(otherChrom1.equals(otherChrom2), "That chromosome must not be equal");
	}
	
	//@Test
	public void corssoverWithIllegalChromosome() {
		final PartiallyMatchedCrossover<Integer> pmco = new PartiallyMatchedCrossover<>(1);
			
		final int length = 1000;
		final Array<Integer> alleles = new Array<Integer>(length).fill(Int());
		final ISeq<Integer> ialleles = alleles.toISeq();
		
		final Array<PermutationGene<Integer>> that = alleles.map(PermutationGene.ToGene(ialleles));
		final Array<PermutationGene<Integer>> other = alleles.map(PermutationGene.ToGene(ialleles));
		
		pmco.crossover(that, other);
		
	}
	
	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes, 
		final Integer nchromosomes, 
		final Integer npopulation,
		final Double p
	) {		
		final Population<PermutationGene<Float64>, Float64> population = newPermutationFloat64GenePopulation(
				ngenes, nchromosomes, npopulation
			);
		
		// The mutator to test.
		final PartiallyMatchedCrossover<Float64> crossover = new PartiallyMatchedCrossover<>(p);
		
		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 100;
		final double mean = crossover.getOrder()*npopulation*p;
		
		final long min = 0;
		final long max = nallgenes;
		final Range<Long> domain = new Range<>(min, max);
		
		final Histogram<Long> histogram = Histogram.valueOf(min, max, 10);	
		final Variance<Long> variance = new Variance<>();
		
		for (int i = 0; i < N; ++i) {
			final long alterations = crossover.alter(population, 1);
			histogram.accumulate(alterations);
			variance.accumulate(alterations);	
		}
				
		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<>(domain, mean, variance.getVariance()));
	}
	
	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}
	
}









