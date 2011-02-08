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

import java.util.Random;

import javolution.context.LocalContext;

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
public class CharacterChromosomeTest extends ObjectTester { 
    
	private final Factory<?> _factory = new CharacterChromosome(500);
	@Override protected Factory<?> getFactory() {
		return _factory;
	}

	
	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstance() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final CharSet characters = new CharSet("0123456789");
			final CharacterChromosome chromosome = new CharacterChromosome(characters, 5000);
			
			final Histogram<Long> histogram = Histogram.valueOf(0L, 10L, 10);
			
			for (CharacterGene gene : chromosome) {
				histogram.accumulate(Long.valueOf(gene.getAllele().toString()));
			}
			
			
			// Chi-Square teset for gene distribution.
			// http://de.wikibooks.org/wiki/Mathematik:_Statistik:_Tabelle_der_Chi-Quadrat-Verteilung
			assertDistribution(histogram, new UniformDistribution<Long>(0L, 10L));
		} finally {
			LocalContext.exit();
		}
    }
	
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

}




