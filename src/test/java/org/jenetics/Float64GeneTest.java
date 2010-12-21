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

import java.io.IOException;
import java.util.Random;

import javolution.context.LocalContext;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Float64GeneTest { 
    
	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstance() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final Float64 min = Float64.ZERO;
			final Float64 max = Float64.valueOf(100);
			final Factory<Float64Gene> factory = Float64Gene.valueOf(min, max);
			
			final Variance<Float64> variance = new Variance<Float64>();
			 
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
			
			// Chi-Square teset for gene distribution.
			// http://de.wikibooks.org/wiki/Mathematik:_Statistik:_Tabelle_der_Chi-Quadrat-Verteilung
			assertDistribution(histogram, new UniformDistribution<Float64>(min, max));
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
    public void plus() {
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final Float64Gene template = Float64Gene.valueOf(min, max);
		
		for (int i = 0; i < 500; ++i) {
			final Float64Gene a = template.newInstance(i);
			final Float64Gene b = template.newInstance(i);
			final Float64Gene c = a.plus(b);
			
			assertEquals(a.getMin().doubleValue(), min);
			assertEquals(a.getMax().doubleValue(), max);
			assertEquals(b.getMin().doubleValue(), min);
			assertEquals(b.getMax().doubleValue(), max);
			assertEquals(c.getMin().doubleValue(), min);
			assertEquals(c.getMax().doubleValue(), max);
			assertEquals(c.getAllele().doubleValue(), (double)i+i);
		}
    }

	@Test
    public void minus() {
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final Float64Gene template = Float64Gene.valueOf(min, max);
		
		for (int i = -500; i < 500; ++i) {
			final Float64Gene a = template.newInstance(i);
			final Float64Gene b = template.newInstance(i);
			final Float64Gene c = a.minus(b);
			
			assertEquals(a.getMin().doubleValue(), min);
			assertEquals(a.getMax().doubleValue(), max);
			assertEquals(b.getMin().doubleValue(), min);
			assertEquals(b.getMax().doubleValue(), max);
			assertEquals(c.getMin().doubleValue(), min);
			assertEquals(c.getMax().doubleValue(), max);;
			assertEquals(c.getAllele().doubleValue(), (double)i-i);
		}
    }

	@Test
    public void times() {
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final Float64Gene template = Float64Gene.valueOf(min, max);
		
		for (int i = -500; i < 500; ++i) {
			final Float64Gene a = template.newInstance(i);
			final Float64Gene b = template.newInstance(i);
			final Float64Gene c = a.times(b);
			
			assertEquals(a.getMin().doubleValue(), min);
			assertEquals(a.getMax().doubleValue(), max);
			assertEquals(b.getMin().doubleValue(), min);
			assertEquals(b.getMax().doubleValue(), max);
			assertEquals(c.getMin().doubleValue(), min);
			assertEquals(c.getMax().doubleValue(), max);
			assertEquals(c.getAllele().doubleValue(), (double)i*i);
		}
    }
	
	@Test
    public void divide() {
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final Float64Gene template = Float64Gene.valueOf(min, max);
		
		for (int i = 1; i < 500; ++i) {
			final Float64Gene a = template.newInstance(i);
			final Float64Gene b = template.newInstance(i + 3);
			final Float64Gene c = a.divide(b);
			
			assertEquals(a.getMin().doubleValue(), min);
			assertEquals(a.getMax().doubleValue(), max);
			assertEquals(b.getMin().doubleValue(), min);
			assertEquals(b.getMax().doubleValue(), max);
			assertEquals(c.getMin().doubleValue(), min);
			assertEquals(c.getMax().doubleValue(), max);
			assertEquals(c.getAllele().doubleValue(), (double)i/(i + 3.0));
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
    public void compareTo() {
        Float64Gene g1 = Float64Gene.valueOf(3.123, 0.123, 5.123);
        Float64Gene g2 = Float64Gene.valueOf(4.123, 1.123, 7.123);
        Float64Gene g3 = Float64Gene.valueOf(3.123, 0.123, 5.123);
        
        assertTrue(g1.compareTo(g2) < 0);
        assertTrue(g2.compareTo(g1) > 0);
        assertTrue(g1.compareTo(g1) == 0);
        assertTrue(g3.compareTo(g1) == 0);
    }

	@Test
    public void testHashCode() {
        Float64Gene g1 = Float64Gene.valueOf(3.345, 0.345, 5.345);
        Float64Gene g2 = Float64Gene.valueOf(4.345, 1.345, 7.345);
        Float64Gene g3 = Float64Gene.valueOf(3.345, 0.345, 5.345);
        
        assertTrue(g1.hashCode() == g3.hashCode());
        assertTrue(g1.hashCode() != g2.hashCode());
    }

	@Test
    public void equalsObject() {
        Float64Gene g1 = Float64Gene.valueOf(4.567, 2.567, 5.567);
        Float64Gene g2 = Float64Gene.valueOf(4.567, 1.567, 7.567);
        Float64Gene g3 = Float64Gene.valueOf(4.567, 2.567, 5.567);
        
        assertTrue(g1.equals(g3));
        assertFalse(g1.equals(g2));
        assertFalse(g2.equals(g1));
        assertFalse(g2.equals(null));
        assertFalse(g2.equals(""));
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

	@Test
    public void testToString() {
        Float64Gene g1 = Float64Gene.valueOf(3.5, 0.5, 5.5);
        
        assertTrue(g1.toString() != null);
        assertTrue(g1.toString().length() > 0);
    }
	
	@Test
	public void xmlSerialize() throws XMLStreamException {
		SerializeUtils.testXMLSerialization(Float64Gene.valueOf(3.5, 0.5, 5.5));
		SerializeUtils.testXMLSerialization(Float64Gene.valueOf(Math.PI, 0.5, 5.5));
	}
	
	@Test
	public void objectSerialize() throws IOException {
		SerializeUtils.testSerialization(Float64Gene.valueOf(3.5, 0.5, 5.5));
		SerializeUtils.testSerialization(Float64Gene.valueOf(Math.PI, 0.5, 5.5));
	}
}







