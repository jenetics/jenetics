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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import javolution.context.LocalContext;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.CharSet;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class CharacterGeneTest extends ObjectTester { 
    
	private final Factory<?> _factory = CharacterGene.valueOf();
	@Override protected Factory<?> getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstance() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final CharSet characters = new CharSet("0123456789");
			
			final Factory<CharacterGene> factory = CharacterGene.valueOf(characters);
			
			final Histogram<Long> histogram = Histogram.valueOf(0L, 10L, 10);
			
			final int samples = 100000;
			for (int i = 0; i < samples; ++i) {
				final CharacterGene g1 = factory.newInstance();
				final CharacterGene g2 = factory.newInstance();				
				Assert.assertNotSame(g1, g2);
								
				histogram.accumulate(Long.valueOf(g1.getAllele().toString()));
				histogram.accumulate(Long.valueOf(g2.getAllele().toString()));
			}
			
			// Chi-Square teset for gene distribution.
			// http://de.wikibooks.org/wiki/Mathematik:_Statistik:_Tabelle_der_Chi-Quadrat-Verteilung
			assertDistribution(histogram, new UniformDistribution<Long>(0L, 10L));
		} finally {
			LocalContext.exit();
		}
	}

    @Test
    public void testCharacterGene() {
        CharacterGene gene = CharacterGene.valueOf();
        assertTrue(gene.isValidCharacter(gene.getAllele()));
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
        for (Character c : CharacterGene.DEFAULT_CHARACTERS) {
            assertTrue(CharacterGene.valueOf(c).isValidCharacter(c));
        }
    }

    @Test
    public void testGetValidCharacters() {
        CharSet cset = CharacterGene.DEFAULT_CHARACTERS;
        assertNotNull(cset);
        assertFalse(cset.isEmpty());
    }

}




