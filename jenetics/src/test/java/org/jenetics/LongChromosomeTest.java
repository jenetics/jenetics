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

import static org.jenetics.stat.StatisticsAssert.assertUniformDistribution;
import static org.jenetics.util.RandomRegistry.using;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LongChromosomeTest
	extends NumericChromosomeTester<Long, LongGene>
{

	private final LongChromosome _factory = new LongChromosome(
		0L, Long.MAX_VALUE, 500
	);

	@Override
	protected LongChromosome factory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		using(new Random(12345), r -> {
			final long min = 0;
			final long max = 10000000;

			final MinMax<Long> mm = MinMax.of();
			final Histogram<Long> histogram = Histogram.ofLong(min, max, 10);

			for (int i = 0; i < 1000; ++i) {
				final LongChromosome chromosome = new LongChromosome(min, max, 500);
				for (LongGene gene : chromosome) {
					mm.accept(gene.getAllele());
					histogram.accept(gene.getAllele());
				}
			}

			Assert.assertTrue(mm.getMin().compareTo(0L) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100L) <= 100);
			assertUniformDistribution(histogram);
		});
	}

}
