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

import static org.jenetics.util.Accumulators.accumulate;

import java.io.IOException;
import java.util.Random;

import javolution.context.LocalContext;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Accumulators.MinMax;
import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Integer64ChromosomeTest {

	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstance() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final Integer64 min = Integer64.ZERO;
			final Integer64 max = Integer64.valueOf(100);
			final Integer64Chromosome chromosome = new Integer64Chromosome(min, max, 5000);
			
			final MinMax<Integer64> mm = new MinMax<Integer64>();			
			final Variance<Integer64> variance = new Variance<Integer64>();
			final Histogram<Integer64> histogram = Histogram.valueOf(min, max, 10);
			
			accumulate(
					chromosome, 
					mm.adapt(Integer64Gene.Value),
					variance.adapt(Integer64Gene.Value),
					histogram.adapt(Integer64Gene.Value)
				);
			
			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			
			// Chi-Square teset for gene distribution.
			// http://de.wikibooks.org/wiki/Mathematik:_Statistik:_Tabelle_der_Chi-Quadrat-Verteilung
			final UniformDistribution<Integer64> dist =
				new UniformDistribution<Integer64>(min, max);
			
			final double χ2 = histogram.χ2(dist.cdf());
			Assert.assertTrue(χ2 < 25); // TODO: Remove magic number.
		} finally {
			LocalContext.exit();
		}
    }
	
	@Test
	public void equals() {
		Integer64Chromosome c1 = new Integer64Chromosome(0, 100, 10);
		Integer64Chromosome c2 = new Integer64Chromosome(0, 100, 10);
		Assert.assertFalse(c1.equals(c2));
		
		c2 = new Integer64Chromosome(c1.toArray());
		Assert.assertEquals(c2, c1);
		Assert.assertNotSame(c1, c2);
		
		c2 = new Integer64Chromosome(c1.toArray().copy());
		Assert.assertEquals(c2, c1);
		Assert.assertNotSame(c1, c2);
	}
	
	@Test
	public void xmlSerialize() throws XMLStreamException {
		SerializeUtils.testXMLSerialization(new Integer64Chromosome(0, 100, 10));
	}
	
	@Test
	public void objectSerialize() throws IOException {
		SerializeUtils.testSerialization(new Integer64Chromosome(0, 100, 10));
	}
	
}




