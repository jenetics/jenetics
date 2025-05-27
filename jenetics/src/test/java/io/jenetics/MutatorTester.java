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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static io.jenetics.TestUtils.diff;
import static io.jenetics.TestUtils.newDoubleGenePopulation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.Interval;
import io.jenetics.stat.LongMomentStatistics;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public abstract class MutatorTester extends AltererTester {

	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final ISeq<Phenotype<DoubleGene, Double>> p1 =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		final Alterer<DoubleGene, Double> mutator = newAlterer(0.01);
		final AltererResult<DoubleGene, Double> result = mutator.alter(p1, 1);

		int mutations = result.alterations();
		int difference = diff(p1, result.population());

		Assert.assertEquals(
			mutations, difference,
			String.format("diff=%s, mutations=%s", difference, mutations)
		);
	}

	@Test(dataProvider = "alterProbabilityParameters", groups = {"statistics"})
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final ISeq<Phenotype<DoubleGene, Double>> population =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		// The mutator to test.
		final Alterer<DoubleGene, Double> mutator = newAlterer(p);

		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 100;
		final double mean = nallgenes*p;

		final long min = 0;
		final long max = nallgenes;

		final var histogram = Histogram.Builder.of(new Interval(min, max), 10);
		final var variance = new LongMomentStatistics();

		for (int i = 0; i < N; ++i) {
			final long alterations = mutator
				.alter(population.copy(), 1)
				.alterations();

			histogram.add(alterations);
			variance.accept(alterations);
		}

		// Normal distribution as approximation for binomial distribution.
		// TODO: Implement test
//		assertDistribution(
//			histogram,
//			new NormalDistribution<>(domain, mean, variance.getVariance())
//		);
	}

	public double var(final double p, final long N) {
		return N*p*(1.0 - p);
	}

	public double mean(final double p, final long N) {
		return N*p;
	}

	@DataProvider(name = "alterCountParameters")
	public Object[][] alterCountParameters() {
		return TestUtils.alterCountParameters();
	}

	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}

}
