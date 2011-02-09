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
import static org.jenetics.util.Accumulators.accumulate;

import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Accumulators.MinMax;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Integer64ChromosomeTest extends ChromosomeTester<Integer64Gene> { 
    
	private final Factory<Chromosome<Integer64Gene>> 
	_factory = new Integer64Chromosome(0, Long.MAX_VALUE, 500);
	@Override protected Factory<Chromosome<Integer64Gene>> getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random());
			
			final Integer64 min = Integer64.ZERO;
			final Integer64 max = Integer64.valueOf(10000000);
			
			final MinMax<Integer64> mm = new MinMax<Integer64>();			
			final Variance<Integer64> variance = new Variance<Integer64>();
			final Histogram<Integer64> histogram = Histogram.valueOf(min, max, 10);
			
			for (int i = 0; i < 1000; ++i) {
				final Integer64Chromosome chromosome = new Integer64Chromosome(min, max, 500);
				
				accumulate(
						chromosome, 
						mm.adapt(Integer64Gene.Value),
						variance.adapt(Integer64Gene.Value),
						histogram.adapt(Integer64Gene.Value)
					);
			}
			
			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			
			// Chi-Square teset for gene distribution.
			// http://de.wikibooks.org/wiki/Mathematik:_Statistik:_Tabelle_der_Chi-Quadrat-Verteilung
			assertDistribution(histogram, new UniformDistribution<Integer64>(min, max));
		} finally {
			LocalContext.exit();
		}
    }
	
}




