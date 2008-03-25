/*
 * Java Genetic Algorithm Library (Jenetics-0.1.0.3).
 * Copyright (c) 2007 Franz Wilhelmstötter
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

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleGeneTest.java,v 1.2 2008-03-25 18:57:54 fwilhelm Exp $
 */
public class DoubleGeneTest { 
    
	@Test
    public void testDoubleGeneIntegerIntegerInteger() {
        DoubleGene gene = DoubleGene.valueOf(1.234, 0.345, 2.123);
        assertEquals(gene.getAllele().doubleValue(), 1.234);
        assertEquals(gene.getMinValue().doubleValue(), 0.345);
        assertEquals(gene.getMaxValue().doubleValue(), 2.123);
        
        try {
            gene = DoubleGene.valueOf(0.1, 2.1, 4.1);
            assertFalse(gene.isValid());
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } 
    }

	@Test
    public void testDoubleGeneIntegerInteger() {
        DoubleGene gene = DoubleGene.valueOf(-10.567, 10.567);
        assertEquals(gene.getMinValue().doubleValue(), -10.567);
        assertEquals(gene.getMaxValue().doubleValue(), 10.567);
    }

	@Test
    public void testAdd() {
        DoubleGene g1 = DoubleGene.valueOf(34.456, 0.456, 100.456);
        DoubleGene g2 = DoubleGene.valueOf(2.0, 1.0, 10.0);
        DoubleGene g3 = g1.plus(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 36.456);
        assertEquals(g3.getMinValue().doubleValue(), 0.456);
        assertEquals(g3.getMaxValue().doubleValue(), 100.456);
    }

	@Test
    public void testSub() {
        DoubleGene g1 = DoubleGene.valueOf(34.123, 10.123, 99.123);
        DoubleGene g2 = DoubleGene.valueOf(2.0, 1.0, 10.0);
        NumberGene<Float64> g3 = g1.minus(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 32.123);
        assertEquals(g3.getMinValue().doubleValue(), 10.123);
        assertEquals(g3.getMaxValue().doubleValue(), 99.123);
    }

	@Test
    public void testMul() {
        DoubleGene g1 = DoubleGene.valueOf(34.345, 10.345, 99.345);
        DoubleGene g2 = DoubleGene.valueOf(2.0, 1.0, 10.0);
        DoubleGene g3 = g1.times(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 68.690);
        assertEquals(g3.getMinValue().doubleValue(), 10.345);
        assertEquals(g3.getMaxValue().doubleValue(), 99.345);
    }

	@Test
    public void testDiv() {
        DoubleGene g1 = DoubleGene.valueOf(34.222, 10.222, 99.222);
        DoubleGene g2 = DoubleGene.valueOf(2.0, 1.0, 10.0);
        DoubleGene g3 = g1.divide(g2);
        
        assertEquals(g3.getAllele().doubleValue(), 17.111);
        assertEquals(g3.getMinValue().doubleValue(), 10.222);
        assertEquals(g3.getMaxValue().doubleValue(), 99.222);
    }
	
	@Test
	public void testMean() {
		DoubleGene g1 = DoubleGene.valueOf(1, 0, 20);
		DoubleGene g5 = DoubleGene.valueOf(5, 0, 20);
		
		DoubleGene g = g5.mean(g1);
		assertEquals(g.doubleValue(), 3.0);
	}

	@Test
    public void testCreateNumber() {
        DoubleGene gene = DoubleGene.valueOf(1.2345, -1234.1234, 1234.1234);
        DoubleGene g2 = gene.newInstance(5);
        
        assertEquals(g2.getAllele().intValue(), 5);
        assertEquals(g2.getMinValue().doubleValue(), -1234.1234);
        assertEquals(g2.getMaxValue().doubleValue(), 1234.1234);
    }

	@Test
    public void testCompareTo() {
        DoubleGene g1 = DoubleGene.valueOf(3.123, 0.123, 5.123);
        DoubleGene g2 = DoubleGene.valueOf(4.123, 1.123, 7.123);
        DoubleGene g3 = DoubleGene.valueOf(3.123, 0.123, 5.123);
        
        assertTrue(g1.compareTo(g2) < 0);
        assertTrue(g2.compareTo(g1) > 0);
        assertTrue(g1.compareTo(g1) == 0);
        assertTrue(g3.compareTo(g1) == 0);
    }

	@Test
    public void testHashCode() {
        DoubleGene g1 = DoubleGene.valueOf(3.345, 0.345, 5.345);
        DoubleGene g2 = DoubleGene.valueOf(4.345, 1.345, 7.345);
        DoubleGene g3 = DoubleGene.valueOf(3.345, 0.345, 5.345);
        
        assertTrue(g1.hashCode() == g3.hashCode());
        assertTrue(g1.hashCode() != g2.hashCode());
    }

	@Test
    public void testEqualsObject() {
        DoubleGene g1 = DoubleGene.valueOf(4.567, 2.567, 5.567);
        DoubleGene g2 = DoubleGene.valueOf(4.567, 1.567, 7.567);
        DoubleGene g3 = DoubleGene.valueOf(4.567, 2.567, 5.567);
        
        assertTrue(g1.equals(g3));
        assertFalse(g1.equals(g2));
        assertFalse(g2.equals(g1));
        assertFalse(g2.equals(null));
        assertFalse(g2.equals(""));
    }

	@Test
    public void testGetMinValue() {
        DoubleGene g1 = DoubleGene.valueOf(3.1, 0.1, 5.1);
        DoubleGene g2 = DoubleGene.valueOf(4.1, 1.1, 7.1);
        DoubleGene g3 = DoubleGene.valueOf(3.1, 0.1, 5.1);
        
        assertEquals(g1.getMinValue().doubleValue(), 0.1);
        assertEquals(g2.getMinValue().doubleValue(), 1.1);
        assertEquals(g3.getMinValue().doubleValue(), 0.1);
    }

	@Test
    public void testGetMaxValue() {
        DoubleGene g1 = DoubleGene.valueOf(3.2, 0.2, 5.2);
        DoubleGene g2 = DoubleGene.valueOf(4.2, 1.2, 7.2);
        DoubleGene g3 = DoubleGene.valueOf(3.2, 0.2, 5.2);
        
        assertEquals(g1.getMaxValue().doubleValue(), 5.2);
        assertEquals(g2.getMaxValue().doubleValue(), 7.2);
        assertEquals(g3.getMaxValue().doubleValue(), 5.2);
    }

	@Test
    public void testToString() {
        DoubleGene g1 = DoubleGene.valueOf(3.5, 0.5, 5.5);
        
        assertTrue(g1.toString() != null);
        assertTrue(g1.toString().length() > 0);
    }
}
