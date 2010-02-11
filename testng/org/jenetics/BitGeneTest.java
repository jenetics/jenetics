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
import javolution.xml.stream.XMLStreamException;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class BitGeneTest {

    @Test
    public void testHashCode() {
        assertEquals(BitGene.FALSE.hashCode(), BitGene.ZERO.hashCode());
        assertFalse(BitGene.TRUE.hashCode() == BitGene.FALSE.hashCode());
    }

    @Test
    public void testGetValue() {
        assertEquals(BitGene.FALSE.getBit(), false);
        assertEquals(BitGene.ZERO.getBit(), false);
        assertEquals(BitGene.TRUE.getBit(), true);
        assertEquals(BitGene.ONE.getBit(), true);
    }

    @Test
    public void testEqualsObject() {
        assertTrue(BitGene.FALSE.equals(BitGene.ZERO));
        assertFalse(BitGene.FALSE.equals(BitGene.TRUE));
        assertFalse(BitGene.TRUE.equals("string"));
        assertFalse(BitGene.ONE.equals(null));
    }

    @Test
    public void testToString() {
        assertFalse("".equals(BitGene.FALSE.toString()));
    }

    @Test
    public void testCompareTo() {
        assertEquals(BitGene.ZERO.compareTo(BitGene.FALSE), 0);
        assertTrue(BitGene.FALSE.compareTo(BitGene.ONE) < 0);
        assertTrue(BitGene.TRUE.compareTo(BitGene.ZERO) > 0);
    }
    
    @Test
    public void serialize() throws XMLStreamException {
    	SerializeUtils.testSerialization(BitGene.TRUE);
    	SerializeUtils.testSerialization(BitGene.FALSE);
    }
    
}
