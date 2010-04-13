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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class GenotypeTest {

    @Test
    public void testHashCode() {
        BitChromosome c1 = BitChromosome.valueOf(12);
        BitChromosome c2 = BitChromosome.valueOf(12);
        BitChromosome c3 = c2.copy();
        Genotype<BitGene> g1 = Genotype.valueOf(c1, c2, c3);
        Genotype<BitGene> g2 = Genotype.valueOf(c2, c3);
        Genotype<BitGene> g3 = g2;
        
        assertFalse(g1.equals(g2));
        assertFalse(g1.hashCode() == g2.hashCode());
        assertTrue(g2.equals(g3));
        assertTrue(g2.hashCode() == g3.hashCode());
        assertEquals(g2, g3);
    }


    @Test
    public void testGenotypeGenotypeOfT() {
        BitChromosome c1 = BitChromosome.valueOf(12);
        BitChromosome c2 = BitChromosome.valueOf(12);
        BitChromosome c3 = c2.copy();
        Genotype<BitGene> g2 = Genotype.valueOf(c1, c2, c3);
        Genotype<BitGene> g4 = Genotype.valueOf(g2);
        
        assertEquals(g2, g4);
        assertEquals(g2.hashCode(), g4.hashCode());
    }

    @Test
    public void testSetGetChromosome() {
        Integer64Chromosome c1 = new Integer64Chromosome(0, 100, 10);
        Integer64Chromosome c2 = new Integer64Chromosome(0, 100, 10);
        @SuppressWarnings("unused")
		Integer64Chromosome c3 = new Integer64Chromosome(0, 100, 10);
        @SuppressWarnings("unused")
		Genotype<Integer64Gene> g = Genotype.valueOf(c1, c2);
    }


    @Test
    public void testCreate() {
        Integer64Chromosome c1 = new Integer64Chromosome(0, 100, 10);
        Integer64Chromosome c2 = new Integer64Chromosome(0, 100, 10);
        Genotype<Integer64Gene> g1 = Genotype.valueOf(c1, c2);
        Genotype<Integer64Gene> g2 = g1.newInstance();
        
        assertFalse(g1 == g2);
        assertFalse(g1.equals(g2));
    }

    @Test
    public void serialize() throws XMLStreamException {
        Integer64Chromosome c1 = new Integer64Chromosome(0, 100, 10);
        Integer64Chromosome c2 = new Integer64Chromosome(0, 100, 10);
        Integer64Chromosome c3 = new Integer64Chromosome(0, 100, 10);
        Genotype<Integer64Gene> g1 = Genotype.valueOf(c1, c2, c3);
        
    	SerializeUtils.testSerialization(g1);
    }
    
    @Test
    public void isValid() {
        Integer64Chromosome c1 = new Integer64Chromosome(0, 100, 10);
        Integer64Chromosome c2 = new Integer64Chromosome(0, 100, 10);
        Genotype<Integer64Gene> g1 = Genotype.valueOf(c1, c2);
        Assert.assertTrue(g1.isValid());
        
        c1 = new Integer64Chromosome(
        		Integer64Gene.valueOf(0, 1, 10),
        		Integer64Gene.valueOf(2, 1, 10),
        		Integer64Gene.valueOf(2, 1, 10)
        	);
        c2 = new Integer64Chromosome(
        		Integer64Gene.valueOf(2, 1, 10),
        		Integer64Gene.valueOf(2, 1, 10),
        		Integer64Gene.valueOf(2, 1, 10)
        	);
        g1 = Genotype.valueOf(c1, c2);
        Assert.assertFalse(g1.isValid());
    }

}





