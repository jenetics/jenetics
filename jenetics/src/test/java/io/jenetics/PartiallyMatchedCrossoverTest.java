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

import static io.jenetics.TestUtils.newPermutationDoubleGenePopulation;
import static io.jenetics.util.factories.Int;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.Interval;
import io.jenetics.stat.LongMomentStatistics;
import io.jenetics.distassert.Histogram;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
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
		Assert.assertTrue(thatChrom2.isValid(), "thatChrom2 not valid: " + thatChrom2);

		final PermutationChromosome<Integer> otherChrom2 = new PermutationChromosome<>(other.toISeq());
		Assert.assertTrue(otherChrom2.isValid(), "otherChrom2 not valid: " + otherChrom2);

        Assert.assertNotEquals(thatChrom2, thatChrom1, "That chromosome must not be equal");
        Assert.assertNotEquals(otherChrom2, otherChrom1, "That chromosome must not be equal");
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

	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final ISeq<Phenotype<EnumGene<Double>, Double>> population =
			newPermutationDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		// The mutator to test.
		final var crossover = new PartiallyMatchedCrossover<Double, Double>(p);

		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 100;
		final double mean = crossover.order()*npopulation*p;

		final long min = 0;
		final long max = nallgenes;
		final var interval = new Interval(min, max);

		final var statistics = new LongMomentStatistics();
		final var observation = Histogram.Builder.of(interval, 10)
			.build(samples -> {
				for (int i = 0; i < N; ++i) {
					final long alterations = crossover
						.alter(population, 1)
						.alterations();

					samples.add(alterations);
					statistics.accept(alterations);
				}
			});


		//assertThatObservation(histogram.build())
		//	.isNormal(mean, Math.sqrt(statistics.variance()), new DoubleRange(min, max));
	}

	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}

}
