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

import static org.jenetics.TestUtils.newPermutationDoubleGenePopulation;
import static org.jenetics.util.factories.Int;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.LongMomentStatistics;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class PartiallyMatchedCrossoverTest {

	@Test(invocationCount = 10)
	public void crossover() {
		final PartiallyMatchedCrossover<Integer, Double> pmco =
			new PartiallyMatchedCrossover<>(1);

		final int length = 1000;
		final MSeq<Integer> alleles = MSeq.<Integer>ofLength(length).fill(Int());
		final ISeq<Integer> ialleles = alleles.toISeq();

		final MSeq<EnumGene<Integer>> that = alleles.map(i -> new EnumGene<>(i, ialleles));
		final MSeq<EnumGene<Integer>> other = alleles.map(i -> new EnumGene<>(i, ialleles));

		that.shuffle();
		other.shuffle();

		final PermutationChromosome<Integer> thatChrom1 = new PermutationChromosome<>(that.toISeq());
		Assert.assertTrue(thatChrom1.isValid(), "thatChrom1 not valid");

		final PermutationChromosome<Integer> otherChrom1 = new PermutationChromosome<>(other.toISeq());
		Assert.assertTrue(otherChrom1.isValid(), "otherChrom1 not valid");

		pmco.crossover(that, other);

		final PermutationChromosome<Integer> thatChrom2 = new PermutationChromosome<>(that.toISeq());
		Assert.assertTrue(thatChrom2.isValid(), "thatChrom2 not valid: " + thatChrom2.toSeq());

		final PermutationChromosome<Integer> otherChrom2 = new PermutationChromosome<>(other.toISeq());
		Assert.assertTrue(otherChrom2.isValid(), "otherChrom2 not valid: " + otherChrom2.toSeq());

		Assert.assertFalse(thatChrom1.equals(thatChrom2), "That chromosome must not be equal");
		Assert.assertFalse(otherChrom1.equals(otherChrom2), "That chromosome must not be equal");
	}

	@Test
	public void crossoverWithIllegalChromosome() {
		final PartiallyMatchedCrossover<Integer, Double> pmco = new PartiallyMatchedCrossover<>(1);

		final int length = 1000;
		final MSeq<Integer> alleles = MSeq.<Integer>ofLength(length).fill(Int());
		final ISeq<Integer> ialleles = alleles.toISeq();

		final MSeq<EnumGene<Integer>> that = alleles.map(i -> new EnumGene<>(i, ialleles));
		final MSeq<EnumGene<Integer>> other = alleles.map(i -> new EnumGene<>(i, ialleles));

		pmco.crossover(that, other);

	}

	@Test(dataProvider = "alterProbabilityParameters", groups = {"statistics"})
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final Population<EnumGene<Double>, Double> population =
			newPermutationDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		// The mutator to test.
		final PartiallyMatchedCrossover<Double, Double> crossover = new PartiallyMatchedCrossover<>(p);

		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 100;
		final double mean = crossover.getOrder()*npopulation*p;

		final long min = 0;
		final long max = nallgenes;
		final Range<Long> domain = new Range<>(min, max);

		final Histogram<Long> histogram = Histogram.ofLong(min, max, 10);
		final LongMomentStatistics variance = new LongMomentStatistics();

		for (int i = 0; i < N; ++i) {
			final long alterations = crossover.alter(population, 1);
			histogram.accept(alterations);
			variance.accept(alterations);
		}

		// Normal distribution as approximation for binomial distribution.
		// TODO: Implement test
		//assertDistribution(histogram, new NormalDistribution<>(domain, mean, variance.getVariance()));
	}

	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}

}
