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

import static io.jenetics.ShiftMutatorWithK.getDistance;
import static io.jenetics.TestUtils.newDoubleGenePopulation;

import java.util.DoubleSummaryStatistics;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.LongStream;

import org.apache.commons.rng.sampling.CompositeSamplers;
import org.apache.commons.rng.sampling.shape.LineSampler;
import org.apache.commons.rng.simple.JDKRandomWrapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.Distribution;
import io.jenetics.stat.Histogram;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:feichtenschlager10@gmail.com">Paul Feichtenschlager</a>
 */
public class ShiftMutationWithKTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new ShiftMutatorWithK<>(p);
	}

	@Override
	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final ISeq<Phenotype<DoubleGene, Double>> p1 =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		final MSeq<Phenotype<DoubleGene, Double>> p2 = p1.copy();
		Assert.assertEquals(p2, p1);

		final Alterer<DoubleGene, Double> mutator = newAlterer(0.01);

		final int alterations = mutator.alter(p2, 1).alterations();
		//final int diff = diff(p1, p2);

		if (ngenes == 1) {
			Assert.assertEquals(alterations, 0);
		} else {
			//Assert.assertTrue(alterations >= diff/2, String.format("%d >= %d", alterations, diff/2));
			//Assert.assertTrue(alterations <= 2*diff, String.format("%d < %d", alterations, 2*diff));

		}
	}

	@Override
	@Test(dataProvider = "alterProbabilityParameters", groups = {"statistics"})
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		super.alterProbability(ngenes, nchromosomes, npopulation, p);
	}

	@Override
	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return new Object[][] {
			//    ngenes,       nchromosomes     npopulation
			{180, 1,  150, 0.15},
			{180, 2,  150, 0.15},
			{180, 15, 150, 0.15},

			{180, 1,  150, 0.5},
			{180, 2,  150, 0.5},
			{180, 15, 150, 0.5},

			{180, 1,  150, 0.85},
			{180, 2,  150, 0.85},
			{180, 15, 150, 0.85}
		};
	}

	public static void main(String[] args) {
		final var mutator = new ShiftMutator<DoubleGene, Double>(
			ShiftMutator.ShifterRandom.of(Distribution.linear(0.2))
		);

		/*
		final var histogram = Histogram.ofDouble(0.0, 1.0, 20);
		final var random = new Random();

		final var a = new double[] {0, 0};
		final var b = new double[] {1, 1};

		final var sampler = LineSampler.of(
			new JDKRandomWrapper(random),
			new double[] {0},
			new double[] {1}
		);


		for (int i = 0; i < 10000; ++i) {
			final var value = getDistance(0.8, random);
			//final var value = sampler.sample();
			histogram.accept(value);
		}

		LongStream.of(histogram.getHistogram())
			.forEach(System.out::println);

		 */

		final var random = RandomGenerator.getDefault();
		final var stat = new DoubleSummaryStatistics();
		for (int i = 0; i < 10000; ++i) {
			final var value = getDistance(0.85, random);
			stat.accept(value);
		}

		System.out.println(stat);

	}

}
