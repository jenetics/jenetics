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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Float64GeneTest extends NumberGeneTester<Float64, Float64Gene> { 
    
	private final Factory<Float64Gene> 
	_factory = Float64Gene.valueOf(0, Double.MAX_VALUE);
	@Override protected Factory<Float64Gene> getFactory() {
		return _factory;
	}
	
	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));
			
			final Float64 min = Float64.ZERO;
			final Float64 max = Float64.valueOf(100);
			final Factory<Float64Gene> factory = Float64Gene.valueOf(min, max);
			
			final Variance<Float64> variance = new Variance<>();
			 
			final Histogram<Float64> histogram = Histogram.valueOf(min, max, 10);
			
			final int samples = 100000;
			for (int i = 0; i < samples; ++i) {
				final Float64Gene g1 = factory.newInstance();
				final Float64Gene g2 = factory.newInstance();
				
				assertTrue(g1.getAllele().compareTo(min) >= 0);
				assertTrue(g1.getAllele().compareTo(max) <= 0);
				assertTrue(g2.getAllele().compareTo(min) >= 0);
				assertTrue(g2.getAllele().compareTo(max) <= 0);
				assertFalse(g1.equals(g2));
				Assert.assertNotSame(g1, g2);
				
				variance.accumulate(g1.getAllele());
				variance.accumulate(g2.getAllele());
				histogram.accumulate(g1.getAllele());
				histogram.accumulate(g2.getAllele());
			}
			
			assertDistribution(histogram, new UniformDistribution<>(min, max));
		} finally {
			LocalContext.exit();
		}
	}
	
	@Test
    public void doubleGeneIntegerIntegerInteger() {
        Float64Gene gene = Float64Gene.valueOf(1.234, 0.345, 2.123);
        assertEquals(gene.getAllele().doubleValue(), 1.234);
        assertEquals(gene.getMin().doubleValue(), 0.345);
        assertEquals(gene.getMax().doubleValue(), 2.123);
        
        try {
            gene = Float64Gene.valueOf(0.1, 2.1, 4.1);
            assertFalse(gene.isValid());
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } 
    }

	@Test
    public void doubleGeneIntegerInteger() {
        Float64Gene gene = Float64Gene.valueOf(-10.567, 10.567);
        assertEquals(gene.getMin().doubleValue(), -10.567);
        assertEquals(gene.getMax().doubleValue(), 10.567);
    }
	
	@Test
	public void divide() {
		for (int i = 0; i < 100; ++i) {
			final Float64Gene gene1 = getFactory().newInstance();
			final Float64Gene gene2 = getFactory().newInstance();
			final Float64Gene gene3 = gene1.divide(gene2);
			
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
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final Float64Gene template = Float64Gene.valueOf(min, max);
		
		for (int i = 1; i < 500; ++i) {
			final Float64Gene a = template.newInstance(i);
			final Float64Gene b = template.newInstance(i + 3);
			final Float64Gene c = a.mean(b);
			
			assertEquals(a.getMin().doubleValue(), min);
			assertEquals(a.getMax().doubleValue(), max);
			assertEquals(b.getMin().doubleValue(), min);
			assertEquals(b.getMax().doubleValue(), max);
			assertEquals(c.getMin().doubleValue(), min);
			assertEquals(c.getMax().doubleValue(), max);
			assertEquals(c.getAllele().doubleValue(), (double)((i + (i + 3.0))/2.0));
		}
	}

	@Test
    public void createNumber() {
        Float64Gene gene = Float64Gene.valueOf(1.2345, -1234.1234, 1234.1234);
        Float64Gene g2 = gene.newInstance(5);
        
        assertEquals(g2.getAllele().intValue(), 5);
        assertEquals(g2.getMin().doubleValue(), -1234.1234);
        assertEquals(g2.getMax().doubleValue(), 1234.1234);
    }
	
	@Test
	public void createInvalidNumber() {
		final Float64Gene gene = Float64Gene.valueOf(0, 1, 2);
		Assert.assertFalse(gene.isValid());
	}

	@Test
    public void getMinValue() {
        Float64Gene g1 = Float64Gene.valueOf(3.1, 0.1, 5.1);
        Float64Gene g2 = Float64Gene.valueOf(4.1, 1.1, 7.1);
        Float64Gene g3 = Float64Gene.valueOf(3.1, 0.1, 5.1);
        
        assertEquals(g1.getMin().doubleValue(), 0.1);
        assertEquals(g2.getMin().doubleValue(), 1.1);
        assertEquals(g3.getMin().doubleValue(), 0.1);
    }

	@Test
    public void getMaxValue() {
        Float64Gene g1 = Float64Gene.valueOf(3.2, 0.2, 5.2);
        Float64Gene g2 = Float64Gene.valueOf(4.2, 1.2, 7.2);
        Float64Gene g3 = Float64Gene.valueOf(3.2, 0.2, 5.2);
        
        assertEquals(g1.getMax().doubleValue(), 5.2);
        assertEquals(g2.getMax().doubleValue(), 7.2);
        assertEquals(g3.getMax().doubleValue(), 5.2);
    }

}







