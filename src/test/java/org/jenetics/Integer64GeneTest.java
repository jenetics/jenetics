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

import java.io.IOException;
import java.util.Random;

import javolution.context.LocalContext;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Integer64GeneTest {
	
	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstance() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final Integer64 min = Integer64.ZERO;
			final Integer64 max = Integer64.valueOf(1000000);
			final Factory<Integer64Gene> factory = Integer64Gene.valueOf(min, max);
			
			final Variance<Integer64> variance = new Variance<Integer64>();
			
			final Histogram<Integer64> histogram = Histogram.valueOf(min, max, 10);
			
			final int samples = 10000;
			for (int i = 0; i < samples; ++i) {
				final Integer64Gene g1 = factory.newInstance();
				final Integer64Gene g2 = factory.newInstance();
				
				Assert.assertTrue(g1.getAllele().compareTo(min) >= 0);
				Assert.assertTrue(g1.getAllele().compareTo(max) <= 0);
				Assert.assertTrue(g2.getAllele().compareTo(min) >= 0);
				Assert.assertTrue(g2.getAllele().compareTo(max) <= 0);
				Assert.assertNotSame(g1, g2);
				
				variance.accumulate(g1.getAllele());
				variance.accumulate(g2.getAllele());
				histogram.accumulate(g1.getAllele());
				histogram.accumulate(g2.getAllele());
			}
			
			// Chi-Square teset for gene distribution.
			// http://de.wikibooks.org/wiki/Mathematik:_Statistik:_Tabelle_der_Chi-Quadrat-Verteilung
			assertDistribution(histogram, new UniformDistribution<Integer64>(min, max));
		} finally {
			LocalContext.exit();
		}
	}
	
	@Test
	public void xmlSerialize() throws XMLStreamException {
		SerializeUtils.testXMLSerialization(Integer64Gene.valueOf(5, 0, 10));
		SerializeUtils.testXMLSerialization(Integer64Gene.valueOf(5, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	@Test
	public void objectSerialize() throws IOException {
		SerializeUtils.testSerialization(Integer64Gene.valueOf(5, 0, 10));
		SerializeUtils.testSerialization(Integer64Gene.valueOf(5, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	@Test
	public void set() {
		Integer64Gene gene = Integer64Gene.valueOf(5, 0, 10);
		Assert.assertEquals(gene.getAllele(), Integer64.valueOf(5));
		Assert.assertEquals(gene.getMin(), Integer64.valueOf(0));
		Assert.assertEquals(gene.getMax(), Integer64.valueOf(10));
	}
	
}





