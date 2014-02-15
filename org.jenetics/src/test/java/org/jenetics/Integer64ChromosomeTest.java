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

import org.jscience.mathematics.number.Integer64;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;
import org.jenetics.util.accumulators.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
@SuppressWarnings("deprecation")
public class Integer64ChromosomeTest
	extends NumberChromosomeTester<Integer64, Integer64Gene>
{

	private final Integer64Chromosome
	_factory = new Integer64Chromosome(0, Long.MAX_VALUE, 500);
	@Override protected Integer64Chromosome getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		try (Scoped<Random> s = RandomRegistry.scope(new Random(12345))) {
			final Integer64 min = Integer64.ZERO;
			final Integer64 max = Integer64.valueOf(10000000);

			final MinMax<Integer64> mm = new MinMax<>();
			final Variance<Integer64> variance = new Variance<>();
			final Histogram<Integer64> histogram = Histogram.valueOf(min, max, 10);

			for (int i = 0; i < 1000; ++i) {
				final Integer64Chromosome chromosome = new Integer64Chromosome(min, max, 500);

				accumulate(
						chromosome,
						mm.map(Integer64Gene.Value),
						variance.map(Integer64Gene.Value),
						histogram.map(Integer64Gene.Value)
					);
			}

			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			assertDistribution(histogram, new UniformDistribution<>(min, max));
		}
    }

	@Test
	public void firstGeneConverter() {
		final Integer64Chromosome c = getFactory().newInstance();

		Assert.assertEquals(Integer64Chromosome.Gene.apply(c), c.getGene(0));
	}

	@Test
	public void geneConverter() {
		final Integer64Chromosome c = getFactory().newInstance();

		for (int i = 0; i < c.length(); ++i) {
			Assert.assertEquals(
					Integer64Chromosome.Gene(i).apply(c),
					c.getGene(i)
				);
		}
	}

	@Test
	public void genesConverter() {
		final Integer64Chromosome c = getFactory().newInstance();
		Assert.assertEquals(
				Integer64Chromosome.Genes.apply(c),
				c.toSeq()
			);
	}

}
