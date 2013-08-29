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

import static org.jenetics.TestUtils.diff;
import static org.jenetics.TestUtils.newFloat64GenePopulation;
import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class MeanAltererTest {


	@Test
	public void recombinate() {
		final int ngenes = 11;
		final int nchromosomes = 9;
		final int npopulation = 100;
		final Population<Float64Gene, Float64> p1 = newFloat64GenePopulation(
				ngenes, nchromosomes, npopulation
			);
		final Population<Float64Gene, Float64> p2 = p1.copy();
		final int[] selected = new int[]{3, 34};

		final MeanAlterer<Float64Gene> crossover = new MeanAlterer<>(0.1);
		crossover.recombine(p1, selected, 3);

		Assert.assertEquals(diff(p1, p2), ngenes);
	}

	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final Population<Float64Gene, Float64> population = newFloat64GenePopulation(
				ngenes, nchromosomes, npopulation
			);

		// The mutator to test.
		final MeanAlterer<Float64Gene> crossover = new MeanAlterer<>(p);

		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 100;
		final double mean = npopulation*p;

		final long min = 0;
		final long max = nallgenes;
		final Range<Long> domain = new Range<>(min, max);

		final Histogram<Long> histogram = Histogram.valueOf(min, max, 10);
		final Variance<Long> variance = new Variance<>();

		for (int i = 0; i < N; ++i) {
			final long alterations = crossover.alter(population, 1);
			histogram.accumulate(alterations);
			variance.accumulate(alterations);
		}

		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<>(domain, mean, variance.getVariance()));
	}

	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}
}




