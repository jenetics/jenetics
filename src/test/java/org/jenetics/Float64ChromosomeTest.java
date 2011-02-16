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
import static org.jenetics.util.Accumulators.accumulate;

import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Accumulators.MinMax;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Float64ChromosomeTest 
	extends NumberChromosomeTester<Float64, Float64Gene> 
{ 
    
	private final Float64Chromosome 
	_factory = new Float64Chromosome(0, Double.MAX_VALUE, 500);
	@Override protected Float64Chromosome getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final Float64 min = Float64.ZERO;
			final Float64 max = Float64.valueOf(100);
			
			
			final MinMax<Float64> mm = new MinMax<Float64>();
			final Histogram<Float64> histogram = Histogram.valueOf(min, max, 10);
			final Variance<Float64> variance = new Variance<Float64>();
			
			for (int i = 0; i < 1000; ++i) {
				final Float64Chromosome chromosome = new Float64Chromosome(min, max, 500);
				
				accumulate(
						chromosome, 
						mm.adapt(Float64Gene.Value),
						histogram.adapt(Float64Gene.Value),
						variance.adapt(Float64Gene.Value)
					);
			}
			
			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			assertDistribution(histogram, new UniformDistribution<Float64>(min, max));
		} finally {
			LocalContext.exit();
		}
    }

	@Test
	public void firstGeneConverter() {
		final Float64Chromosome c = getFactory().newInstance();
		
		Assert.assertEquals(Float64Chromosome.Gene.convert(c), c.getGene(0));
	}
	
	@Test
	public void geneConverter() {
		final Float64Chromosome c = getFactory().newInstance();
		
		for (int i = 0; i < c.length(); ++i) {
			Assert.assertEquals(
					Float64Chromosome.Gene(i).convert(c), 
					c.getGene(i)
				);
		}
	}
	
	@Test
	public void genesConverter() {
		final Float64Chromosome c = getFactory().newInstance();
		Assert.assertEquals(
				Float64Chromosome.Genes.convert(c), 
				c.toArray()
			);
	}
	
}






