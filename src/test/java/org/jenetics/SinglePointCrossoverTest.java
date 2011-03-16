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

import static org.jenetics.TestUtils.newFloat64GenePopulation;
import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Distribution.Domain;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.CharSet;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class SinglePointCrossoverTest {

	private static final class ConstRandom extends Random {
		private static final long serialVersionUID = 1L;
		private final int _value;
		
		public ConstRandom(final int value) {
			_value = value;
		}
		
		@Override
		public int nextInt() {
			return _value;
		}

		@Override
		public int nextInt(int n) {
			return _value;
		}
		
	}
	
	@Test
	public void crossover() {		
		final CharSet chars = CharSet.valueOf("a-zA-Z");
		
		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();
		
		LocalContext.enter();
		try {
			final SinglePointCrossover<CharacterGene> 
			crossover = new SinglePointCrossover<CharacterGene>();
			
			int rv = 12;
			RandomRegistry.setRandom(new ConstRandom(rv));
			MSeq<CharacterGene> g1c = g1.copy();
			MSeq<CharacterGene> g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c.subSeq(0, rv), g2.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));
			
			rv = 0;
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c, g2);
			Assert.assertEquals(g2c, g1);
			Assert.assertEquals(g1c.subSeq(0, rv), g2.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));

			rv = 1;
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c.subSeq(0, rv), g2.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));
			
			rv = g1.length();
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c, g2);
			Assert.assertEquals(g2c, g1);
			Assert.assertEquals(g1c.subSeq(0, rv), g2.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));
		} finally {
			LocalContext.exit();
		}
	}
	
	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes, 
		final Integer nchromosomes, 
		final Integer npopulation,
		final Double p
	) {		
		final Population<Float64Gene, Float64> population = newFloat64GenePopulation(
				ngenes, nchromosomes, npopulation
			);
		
		// The mutator to test.
		final SinglePointCrossover<Float64Gene> crossover = new SinglePointCrossover<Float64Gene>(p);
		
		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 200;
		final double mean = crossover.getOrder()*npopulation*p;
		
		final long min = 0;
		final long max = nallgenes;
		final Domain<Long> domain = new Domain<Long>(min, max);
		
		final Histogram<Long> histogram = Histogram.valueOf(min, max, 10);	
		final Variance<Long> variance = new Variance<Long>();
		
		for (int i = 0; i < N; ++i) {
			final long alterations = crossover.alter(population, 1);
			histogram.accumulate(alterations);
			variance.accumulate(alterations);	
		}
				
		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<Long>(domain, mean, variance.getVariance()));
	}
	
	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}
	
}


















