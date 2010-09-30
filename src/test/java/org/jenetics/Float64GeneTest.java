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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Random;

import javolution.context.LocalContext;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Accumulators.Variance;
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
    
	@Test(invocationCount = 10)
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
			final UniformDistribution<Float64> dist =
				new UniformDistribution<Float64>(min, max);
			
			final double χ2 = histogram.χ2(dist.cdf());
			Assert.assertTrue(χ2 < 25); // TODO: Remove magic number.
		} finally {
			LocalContext.exit();
		}
	}
	
	@Test
    public void testDoubleGeneIntegerIntegerInteger() {
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
    public void testDoubleGeneIntegerInteger() {
        Float64Gene gene = Float64Gene.valueOf(-10.567, 10.567);
        assertEquals(gene.getMin().doubleValue(), -10.567);
        assertEquals(gene.getMax().doubleValue(), 10.567);
    }

	@Test
    public void testAdd() {
        Float64Gene g1 = Float64Gene.valueOf(34.456, 0.456, 100.456);
        Float64Gene g2 = Float64Gene.valueOf(2.0, 1.0, 10.0);
        Float64Gene g3 = g1.plus(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 36.456);
        assertEquals(g3.getMin().doubleValue(), 0.456);
        assertEquals(g3.getMax().doubleValue(), 100.456);
    }

	@Test
    public void testSub() {
        Float64Gene g1 = Float64Gene.valueOf(34.123, 10.123, 99.123);
        Float64Gene g2 = Float64Gene.valueOf(2.0, 1.0, 10.0);
        NumberGene<Float64, ?> g3 = g1.minus(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 32.123);
        assertEquals(g3.getMin().doubleValue(), 10.123);
        assertEquals(g3.getMax().doubleValue(), 99.123);
    }

	@Test
    public void testMul() {
        Float64Gene g1 = Float64Gene.valueOf(34.345, 10.345, 99.345);
        Float64Gene g2 = Float64Gene.valueOf(2.0, 1.0, 10.0);
        Float64Gene g3 = g1.times(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 68.690);
        assertEquals(g3.getMin().doubleValue(), 10.345);
        assertEquals(g3.getMax().doubleValue(), 99.345);
    }
	
	@Test
	public void testMean() {
		Float64Gene g1 = Float64Gene.valueOf(1, 0, 20);
		Float64Gene g5 = Float64Gene.valueOf(5, 0, 20);
		
		Float64Gene g = g5.mean(g1);
		assertEquals(g.doubleValue(), 3.0);
	}

	@Test
    public void testCreateNumber() {
        Float64Gene gene = Float64Gene.valueOf(1.2345, -1234.1234, 1234.1234);
        Float64Gene g2 = gene.newInstance(5);
        
        assertEquals(g2.getAllele().intValue(), 5);
        assertEquals(g2.getMin().doubleValue(), -1234.1234);
        assertEquals(g2.getMax().doubleValue(), 1234.1234);
    }

	@Test
    public void testCompareTo() {
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
    public void testEqualsObject() {
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
    public void testGetMinValue() {
        Float64Gene g1 = Float64Gene.valueOf(3.1, 0.1, 5.1);
        Float64Gene g2 = Float64Gene.valueOf(4.1, 1.1, 7.1);
        Float64Gene g3 = Float64Gene.valueOf(3.1, 0.1, 5.1);
        
        assertEquals(g1.getMin().doubleValue(), 0.1);
        assertEquals(g2.getMin().doubleValue(), 1.1);
        assertEquals(g3.getMin().doubleValue(), 0.1);
    }

	@Test
    public void testGetMaxValue() {
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







