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

import java.util.Random;

import org.jenetics.stat.Distribution.Domain;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class GaussianMutatorTest extends MutatorTestBase {
	
	@Override
	public Alterer<Float64Gene> newAlterer(double p) {
		return new GaussianMutator<Float64Gene>(p);
	}
	
	@Test(invocationCount = 20, successPercentage = 95)
	public void mutate() {
		final Random random = new Random();
		
		final double min = 0;
		final double max = 10;
		final double mean = 5;
		final double var = Math.pow((max - min)/4.0, 2);
		
		final Float64Gene gene = Float64Gene.valueOf(mean, min, max);
		final GaussianMutator<Float64Gene> mutator = new GaussianMutator<Float64Gene>();
		
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 10.0, 10);
		final Variance<Double> variance = new Variance<Double>();
		
		for (int i = 0; i < 10000; ++i) {
			final double value = mutator.mutate(gene, random).doubleValue();
		
			histogram.accumulate(value);
			variance.accumulate(value);
		}
		
		final Domain<Double> domain = new Domain<Double>(min, max);
		assertDistribution(histogram, new NormalDistribution<Double>(domain, mean, var));
	}
	
}



