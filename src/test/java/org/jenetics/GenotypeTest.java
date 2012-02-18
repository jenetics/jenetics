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

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class GenotypeTest extends ObjectTester<Genotype<Float64Gene>> {

	
	private final Factory<Genotype<Float64Gene>> _factory = Genotype.valueOf(
			new Float64Chromosome(0, 1, 50),
			new Float64Chromosome(0, 1, 500),
			new Float64Chromosome(0, 1, 100),
			new Float64Chromosome(0, 1, 50)
		);
	@Override protected Factory<Genotype<Float64Gene>> getFactory() {
		return _factory;
	}

	@Test
	public void factory() {
		final Genotype<Float64Gene> factory = (Genotype<Float64Gene>)_factory;
		final Genotype<Float64Gene> gt = _factory.newInstance();
		
		Assert.assertEquals(factory.length(), gt.length());
		Assert.assertEquals(factory.getNumberOfGenes(), gt.getNumberOfGenes());
		for (int i = 0; i < factory.length(); ++i) {
			Assert.assertEquals(
				factory.getChromosome(i).length(),
				gt.getChromosome(i).length()
			);
		}
	}
	
    @Test
    public void testGenotypeGenotypeOfT() {
        BitChromosome c1 = new BitChromosome(12);
        BitChromosome c2 = new BitChromosome(12);
        BitChromosome c3 = c2.copy();
        Genotype<BitGene> g2 = Genotype.valueOf(c1, c2, c3);
        Genotype<BitGene> g4 = g2;

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
    public void numberOfGenes() {
		final Genotype<Float64Gene> genotype = Genotype.valueOf(
			new Float64Chromosome(0.0, 1.0, 8),
			new Float64Chromosome(1.0, 2.0, 10),
			new Float64Chromosome(0.0, 10.0, 9),
			new Float64Chromosome(0.1, 0.9, 5)
		);
		Assert.assertEquals(genotype.getNumberOfGenes(), 32);
    }

    @Test
    public void newInstance() {
    	final Genotype<Float64Gene> gt1 = Genotype.valueOf(
    			//Rotation
    			new Float64Chromosome(Float64Gene.valueOf(-Math.PI, Math.PI)),
    			
    			//Translation
    			new Float64Chromosome(Float64Gene.valueOf(-300, 300), Float64Gene.valueOf(-300, 300)),
    			
    			//Shear
    			new Float64Chromosome(Float64Gene.valueOf(-0.5, 0.5), Float64Gene.valueOf(-0.5, 0.5))
    		);
    	
    	final Genotype<Float64Gene> gt2 = gt1.newInstance();
    	
    	Assert.assertEquals(gt1.length(), gt2.length());
    	for (int i = 0; i < gt1.length(); ++i) {
    		Chromosome<Float64Gene> ch1 = gt1.getChromosome(i);
    		Chromosome<Float64Gene> ch2 = gt2.getChromosome(i);
    		Assert.assertEquals(ch1.length(), ch2.length());
    	}
    }

}





