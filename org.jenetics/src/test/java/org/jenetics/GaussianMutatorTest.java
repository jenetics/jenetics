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
package org.jenetics;

import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import java.util.Random;

import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class GaussianMutatorTest extends MutatorTestBase {

	@Override
	public Alterer<Float64Gene> newAlterer(double p) {
		return new GaussianMutator<>(p);
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void mutate() {
		final Random random = RandomRegistry.getRandom();

		final double min = 0;
		final double max = 10;
		final double mean = 5;
		final double var = Math.pow((max - min)/4.0, 2);

		final Float64Gene gene = Float64Gene.valueOf(mean, min, max);
		final GaussianMutator<Float64Gene> mutator = new GaussianMutator<>();

		final Histogram<Double> histogram = Histogram.valueOf(0.0, 10.0, 10);
		final Variance<Double> variance = new Variance<>();

		for (int i = 0; i < 10000; ++i) {
			final double value = mutator.mutate(gene, random).doubleValue();

			histogram.accumulate(value);
			variance.accumulate(value);
		}

		final Range<Double> domain = new Range<>(min, max);
		assertDistribution(histogram, new NormalDistribution<>(domain, mean, var));
	}

}



