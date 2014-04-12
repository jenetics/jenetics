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
import static org.jenetics.util.accumulators.accumulate;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Concurrency;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;
import org.jenetics.util.accumulators.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-04-09 $</em>
 */
public class IntegerChromosomeTest
	extends NumericChromosomeTester<Integer, IntegerGene>
{

	private final IntegerChromosome _factory = new IntegerChromosome(
		0, Integer.MAX_VALUE, 500
	);

	@Override
	protected IntegerChromosome getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		try (Scoped<?> s = RandomRegistry.scope(new Random(12345))) {

			final int min = 0;
			final int max = 10000000;

			final MinMax<Integer> mm = new MinMax<>();
			final Variance<Integer> variance = new Variance<>();
			final Histogram<Integer> histogram = Histogram.of(min, max, 10);

			for (int i = 0; i < 1000; ++i) {
				final IntegerChromosome chromosome = new IntegerChromosome(min, max, 500);

				accumulate(
					Concurrency.commonPool(),
					chromosome,
					mm.map(Allele),
					variance.map(Allele),
					histogram.map(Allele)
				);
			}

			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			assertDistribution(histogram, new UniformDistribution<>(min, max));
		}
	}

	private static final Function<IntegerGene, Integer> Allele =
		new Function<IntegerGene, Integer>() {
			@Override public Integer apply(final IntegerGene value) {
				return value.getAllele();
			}
		};

}
