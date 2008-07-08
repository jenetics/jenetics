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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.StringWriter;
import java.util.BitSet;

import javolution.xml.XMLObjectWriter;

import org.jscience.mathematics.number.LargeInteger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BitChromosomeTest.java,v 1.4 2008-07-08 17:02:26 fwilhelm Exp $
 */
public class BitChromosomeTest {

    @Test
    public void testHashCode() {
        BitChromosome c1 = BitChromosome.valueOf(10);
        BitChromosome c2 = BitChromosome.valueOf(10);
        
        assertEquals(c1.equals(c2), c1.hashCode() == c2.hashCode());
    }

    @Test
    public void testNumValue() {
        BitChromosome c1 = BitChromosome.valueOf(10);
        
        int value = c1.intValue();
        assertEquals((short)value, c1.shortValue());
        assertEquals(value, c1.longValue());
        float f = value;
        assertEquals(f, c1.floatValue());
        double d = value;
        assertEquals(d, c1.doubleValue());
    }

    @Test
    public void testBitChromosomeIntProbability() {
        BitChromosome c = BitChromosome.valueOf(10, Probability.valueOf(0));
        for (BitGene g : c) {
            assertFalse(g.getBit());
        }
        
        c = BitChromosome.valueOf(10, Probability.valueOf(1));
        for (BitGene g : c) {
            assertTrue(g.getBit());
        }
    }

    @Test
    public void testBitChromosomeBitSet() {
        BitSet bits = new BitSet(10);
        for (int i = 0; i < 10; ++i) {
            bits.set(i, i % 2 == 0);
        }
        
        BitChromosome c = BitChromosome.valueOf(bits);
        for (int i = 0; i < bits.length(); ++i) {
            assertEquals(c.getGene(i).getBit(), i % 2 == 0);
        }
    }

    @Test
    public void testLength() {
        BitChromosome c = BitChromosome.valueOf(34);
        assertEquals(34, c.length());
    }

    @Test
    public void testIterator() {
        BitChromosome c = BitChromosome.valueOf(17);
        
        int index = 0;
        for (BitGene g : c) {
            assertEquals(c.getGene(index).getBit(), g.getBit());
            ++index;
        }
        assertEquals(c.length(), index);
    }

    @Test
    public void testMutate() {
        BitChromosome c = BitChromosome.valueOf(10);
        
        BitGene g1 = c.getGene(3);
        c.mutate(3);
        BitGene g2 = c.getGene(3);
        assertEquals(g1.getBit(), g2.getBit());
    }

    @Test
    public void testFlip() {
        BitChromosome c = BitChromosome.valueOf(10);
        
        BitGene g1 = c.getGene(3);
        c.flip(3);
        BitGene g2 = c.getGene(3);
        assertEquals(g1.getBit(), g2.getBit());
    }

    @Test
    public void testToBigInteger() {
        BitChromosome c = BitChromosome.valueOf(LargeInteger.valueOf(234902));
        
        LargeInteger i = c.toLargeInteger();
        assertEquals(i, LargeInteger.valueOf(234902));
        assertEquals(i.intValue(), 234902);
        assertEquals(i.longValue(), c.longValue());
        assertEquals(i.intValue(), c.intValue());
        
        byte[] data = new byte[3];
        c.toByteArray(data);
        BitChromosome c2 = BitChromosome.valueOf(data);
        LargeInteger i2 = c2.toLargeInteger();
        assertEquals(i2, LargeInteger.valueOf(234902));
    }

    @Test
    public void testToBitSet() {
        BitChromosome c1 = BitChromosome.valueOf(34);
        BitChromosome c2 = BitChromosome.valueOf(34, c1.toBitSet());
        
        for (int i = 0; i < c1.length(); ++i) {
            assertEquals(c1.getGene(i).getBit(), c2.getGene(i).getBit());
        }
    }

    @Test
    public void testCreate() {
        BitChromosome c1 = BitChromosome.valueOf(23);
        BitChromosome c2 = c1.newChromosome();
        
        assertTrue(c1 != c2);
        assertEquals(c1.length(), c2.length());
    }

    @Test
    public void testEqualsObject() {
        BitChromosome c1 = BitChromosome.valueOf(10);
        BitChromosome c2 = BitChromosome.valueOf(10, c1.toBitSet());
        
        assertTrue(c1.equals(c2));
    }

    @Test
    public void testToString() {
        BitChromosome c = BitChromosome.valueOf(12);
        
        assertNotNull(c.toString());
        assertTrue(c.toString().length() > 0);
    }
    
    @Test
    public void toByteArray() {
    	byte[] data = new byte[16];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)(Math.random()*256);
		}
    	BitChromosome bc = BitChromosome.valueOf(data);
    	
    	Assert.assertEquals(bc.toByteArray(), data);
    	
    }
    
    @Test
    public void serialize() throws Exception {
    	StringWriter out = new StringWriter();
    	
    	BitChromosome chromosome = BitChromosome.valueOf(10);
    	XMLObjectWriter writer = XMLObjectWriter.newInstance(out);
    	writer.setIndentation("    ");
    	
    	writer.write(chromosome);
    	writer.close();
    	out.flush();
    	
    	Reporter.log(out.toString(), true);
    }


}







