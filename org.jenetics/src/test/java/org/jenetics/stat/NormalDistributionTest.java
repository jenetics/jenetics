/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
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
 * @version <em>$Date: 2013-04-27 $</em>
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
