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
package org.jenetics.stat;

import java.util.Random;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class NormalDistributionTest {

	
	@Test(invocationCount = 10)
	public void cdfDistribution() {
		final Random random = RandomRegistry.getRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1000.0, 10);
		final Variance<Double> variance = new Variance<>();
		
		final double mean = 500;
		final double std = 100;
		for (int i = 0; i < 50000; ++i) {
			final double value =  random.nextGaussian()*std + mean;
			histogram.accumulate(value);
			variance.accumulate(value);
		}
		
		final Range<Double> domain = new Range<>(0.0, 1000.0);
		final Distribution<Double> dist = new NormalDistribution<>(domain, mean, std*std);
		
		StatisticsAssert.assertDistribution(histogram, dist);
	}
	
	@Test
	public void pdfToString() {
		final Range<Double> domain = new Range<>(0.0, 100.0);
		final Distribution<Double> dist = new NormalDistribution<>(domain, 50.0, 34.0);
		final Function<Double, Float64> pdf = dist.getPDF();
		
		Assert.assertEquals(pdf.toString(), "p(x) = N[µ=50.000000, σ²=34.000000](x)");
	}
	
	@Test
	public void cdfToString() {
		final Range<Double> domain = new Range<>(0.0, 100.0);
		final Distribution<Double> dist = new NormalDistribution<>(domain, 50.0, 34.0);
		final Function<Double, Float64> cdf = dist.getCDF();
		
		Assert.assertEquals(cdf.toString(), "P(x) = 1/2(1 + erf((x - 50.000000)/(sqrt(2·34.000000))))");
	}
	
}
