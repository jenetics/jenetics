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
import javolution.xml.stream.XMLStreamException;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharacterChromosomeTest.java,v 1.6 2009-02-23 20:58:08 fwilhelm Exp $
 */
public class CharacterChromosomeTest  {

    @Test
    public void testCreate() {
        CharacterChromosome c1 = new CharacterChromosome(34);
        CharacterChromosome c2 = c1.newInstance();
        
        assertEquals(c1.length(), c2.length());
    }

    @Test
    public void testHashCode() {
        CharacterChromosome c1 = new CharacterChromosome(23);
        CharacterChromosome c2 = new CharacterChromosome(23);
        
        assertEquals(c1.equals(c2), c1.hashCode() == c2.hashCode());
    }

    @Test
    public void testIterator() {
        CharacterChromosome c = new CharacterChromosome(17);
        
        int index = 0;
        for (CharacterGene g : c) {
            assertEquals(c.getGene(index), g);
            ++index;
        }
        assertEquals(c.length(), index);
    }

    @Test
    public void serialize() throws XMLStreamException {
    	SerializeUtils.testSerialization(new CharacterChromosome(23));
    }

}




