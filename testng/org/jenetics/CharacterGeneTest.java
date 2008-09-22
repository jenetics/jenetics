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

import java.io.IOException;
import java.util.Set;

import javolution.xml.stream.XMLStreamException;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharacterGeneTest.java,v 1.4 2008-09-22 21:39:49 fwilhelm Exp $
 */
public class CharacterGeneTest {

    @Test
    public void testHashCode() {
        CharacterGene g1 = CharacterGene.valueOf('G');
        CharacterGene g2 = CharacterGene.valueOf('G');
        CharacterGene g3 = CharacterGene.valueOf('t');
        
        assertEquals(g1.hashCode(), g2.hashCode());
        assertTrue(g1.hashCode() != g3.hashCode());
    }

    @Test
    public void testCharacterGene() {
        CharacterGene gene = CharacterGene.valueOf();
        assertTrue(CharacterGene.isValidCharacter(gene.getAllele()));
    }

    @Test
    public void testCharacterGeneCharacter() {
        CharacterGene gene = CharacterGene.valueOf('4');
        
        assertEquals(new Character('4'), gene.getAllele());
    }

    @Test
    public void testGetCharacter() {
        CharacterGene gene = CharacterGene.valueOf('6');
        
        assertEquals(new Character('6'), gene.getAllele());
    }

    @Test
    public void testCompareTo() {
        CharacterGene g1 = CharacterGene.valueOf('1');
        CharacterGene g2 = CharacterGene.valueOf('2');
        CharacterGene g3 = CharacterGene.valueOf('3');
        
        assertTrue(g1.compareTo(g2) < 0);
        assertTrue(g2.compareTo(g3) < 0);
        assertTrue(g3.compareTo(g2) > 0);
        assertTrue(g2.compareTo(g2) == 0);
    }

    @Test
    public void testIsValidCharacter() {
        for (Character c : CharacterGene.getValidCharacters()) {
            assertTrue(CharacterGene.isValidCharacter(c));
        }
    }

    @Test
    public void testGetValidCharacters() {
        Set<Character> cset = CharacterGene.getValidCharacters();
        assertNotNull(cset);
        assertFalse(cset.isEmpty());
    }

    @Test
    public void testEqualsObject() {
        CharacterGene g1 = CharacterGene.valueOf('1');
        CharacterGene g2 = CharacterGene.valueOf('2');
        CharacterGene g3 = CharacterGene.valueOf('3');
        CharacterGene g4 = CharacterGene.valueOf('3');
        
        assertTrue(g4.equals(g3));
        assertTrue(g4.equals(g4));
        assertFalse(g1.equals(g2));
        assertFalse(g2.equals(g3));
        assertFalse(g2.equals(null));
        assertFalse(g3.equals(""));
    }

    @Test
    public void testToString() {
        CharacterGene g1 = CharacterGene.valueOf('1');
        
        assertNotNull(g1.toString());
        assertTrue(g1.toString().length() > 0);
    }
    
    @Test
    public void serialize() throws IOException, XMLStreamException {
    	SerializeUtils.testSerialization(CharacterGene.valueOf());
    }

}




