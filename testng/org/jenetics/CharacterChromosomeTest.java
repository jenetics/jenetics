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

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharacterChromosomeTest.java,v 1.1 2008-03-25 18:31:58 fwilhelm Exp $
 */
public class CharacterChromosomeTest  {

    @Test
    public void testMutate() {
        CharacterChromosome c = CharacterChromosome.valueOf(23);
        CharacterGene g1 = c.getGene(5);
        c.mutate(5);
        CharacterGene g2 = c.getGene(5);
        
        assertEquals(g1, g2);
    }

    @Test
    public void testCreate() {
        CharacterChromosome c1 = CharacterChromosome.valueOf(34);
        CharacterChromosome c2 = c1.newChromosome();
        
        assertEquals(c1.length(), c2.length());
    }

    @Test
    public void testHashCode() {
        CharacterChromosome c1 = CharacterChromosome.valueOf(23);
        CharacterChromosome c2 = CharacterChromosome.valueOf(23);
        
        assertEquals(c1.equals(c2), c1.hashCode() == c2.hashCode());
    }

    @Test
    public void testIterator() {
        CharacterChromosome c = CharacterChromosome.valueOf(17);
        
        int index = 0;
        for (CharacterGene g : c) {
            assertEquals(c.getGene(index), g);
            ++index;
        }
        assertEquals(c.length(), index);
    }


}




