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
import static org.testng.Assert.assertEquals;

import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Integer64GeneTest extends NumberGeneTester<Integer64, Integer64Gene> { 
    
	private final Factory<Integer64Gene> 
	_factory = Integer64Gene.valueOf(0, Long.MAX_VALUE);
	@Override protected Factory<Integer64Gene> getFactory() {
		return _factory;
	}
	
	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));
			
			final Integer64 min = Integer64.ZERO;
			final Integer64 max = Integer64.valueOf(Integer.MAX_VALUE);
			final Factory<Integer64Gene> factory = Integer64Gene.valueOf(min, max);
			
			final Variance<Integer64> variance = new Variance<Integer64>();
			
			final Histogram<Integer64> histogram = Histogram.valueOf(min, max, 10);
			
			final int samples = 10000;
			for (int i = 0; i < samples; ++i) {
				final Integer64Gene g1 = factory.newInstance();
				final Integer64Gene g2 = factory.newInstance();
				
				Assert.assertTrue(g1.getAllele().compareTo(min) >= 0);
				Assert.assertTrue(g1.getAllele().compareTo(max) <= 0);
				Assert.assertTrue(g2.getAllele().compareTo(min) >= 0);
				Assert.assertTrue(g2.getAllele().compareTo(max) <= 0);
				Assert.assertNotSame(g1, g2);
				
				variance.accumulate(g1.getAllele());
				variance.accumulate(g2.getAllele());
				histogram.accumulate(g1.getAllele());
				histogram.accumulate(g2.getAllele());
			}
			
			assertDistribution(histogram, new UniformDistribution<Integer64>(min, max));
		} finally {
			LocalContext.exit();
		}
	}
	
	@Test
    public void createNumber() {
		Integer64Gene gene = Integer64Gene.valueOf(1, 0, 12);
		Integer64Gene g2 = gene.newInstance(5);
        
        assertEquals(g2.getAllele().longValue(), 5);
        assertEquals(g2.getMin().longValue(), 0);
        assertEquals(g2.getMax().longValue(), 12);
    }
	
	@Test
	public void createInvalidNumber() {
		final Integer64Gene gene = Integer64Gene.valueOf(0, 1, 2);
		Assert.assertFalse(gene.isValid());
	}
	
	@Test
	public void divide() {
		for (int i = 0; i < 100; ++i) {
			final Integer64Gene gene1 = getFactory().newInstance();
			final Integer64Gene gene2 = getFactory().newInstance();
			final Integer64Gene gene3 = gene1.divide(gene2);
			
			assertMinMax(gene1, gene2);
			assertMinMax(gene2, gene3);
			assertValid(gene3);
			Assert.assertEquals(
					gene3.getNumber(), 
					gene1.getNumber().divide(gene2.getNumber())
				);
		}
	}
	
	@Test
	public void mean() {
		final long min = -Integer.MAX_VALUE;
		final long max = Integer.MAX_VALUE;
		final Integer64Gene template = Integer64Gene.valueOf(min, max);
		
		for (int i = 1; i < 500; ++i) {
			final Integer64Gene a = template.newInstance(i);
			final Integer64Gene b = template.newInstance(i + 3);
			final Integer64Gene c = a.mean(b);
			
			assertEquals(a.getMin().longValue(), min);
			assertEquals(a.getMax().longValue(), max);
			assertEquals(b.getMin().longValue(), min);
			assertEquals(b.getMax().longValue(), max);
			assertEquals(c.getMin().longValue(), min);
			assertEquals(c.getMax().longValue(), max);
			assertEquals(c.getAllele().longValue(), (long)((i + i + 3)/2));
		}
	}
	
	@Test
	public void set() {
		Integer64Gene gene = Integer64Gene.valueOf(5, 0, 10);
		Assert.assertEquals(gene.getAllele(), Integer64.valueOf(5));
		Assert.assertEquals(gene.getMin(), Integer64.valueOf(0));
		Assert.assertEquals(gene.getMax(), Integer64.valueOf(10));
	}
	
}





