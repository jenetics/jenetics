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

import static io.jenetics.distassert.assertion.Assurance.assertThatObservation;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.distassert.Interval;
import io.jenetics.distassert.observation.Histogram;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GaussianMutatorTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new GaussianMutator<>(p);
	}

	//@Test(invocationCount = 20, successPercentage = 90)
	public void mutate() {
		final var random = RandomRegistry.random();

		final double min = 0;
		final double max = 10;
		final double mean = 5;
		final double var = Math.pow((max - min)/4.0, 2);

		final DoubleGene gene = DoubleGene.of(mean, min, max);
		final GaussianMutator<DoubleGene, Double> mutator = new GaussianMutator<>();

		final var statistics = new DoubleMomentStatistics();
		final var histogram = Histogram.Builder.of(new Interval(0, 10), 10);

		for (int i = 0; i < 100_000; ++i) {
			final double value = mutator.mutate(gene, random).allele();
			statistics.accept(value);
			histogram.add(value);
		}

		assertThatObservation(histogram.build())
			.withinRange(min, max)
			.isNormal(5, Math.sqrt(var));
	}

	@Test
	public void mutateValidGene() {
		final var mutator = new GaussianMutator<DoubleGene, Double>() {
			public DoubleGene mutate(
				final DoubleGene gene,
				final RandomGenerator random
			) {
				return super.mutate(gene, random);
			}
		};

		final var random = new Random() {
			@Override
			public double nextGaussian(double mean, double stddev) {
				return 1;
			}
		};

		DoubleGene gene = DoubleGene.of(0.9, 0, 1);
		Assert.assertTrue(gene.isValid());

		gene = mutator.mutate(gene, random);
		Assert.assertTrue(gene.isValid());
		Assert.assertEquals(gene.doubleValue(), Math.nextDown(1.0));
	}

	@Test
	public void mutateInvalidGene() {
		final var mutator = new GaussianMutator<DoubleGene, Double>() {
			public DoubleGene mutate(
				final DoubleGene gene,
				final RandomGenerator random
			) {
				return super.mutate(gene, random);
			}
		};

		final DoubleGene gene = DoubleGene.of(0.9, 5, 1);
		Assert.assertFalse(gene.isValid());

		final DoubleGene gene1 = mutator.mutate(gene, new Random());
		Assert.assertSame(gene1, gene);
	}

}

