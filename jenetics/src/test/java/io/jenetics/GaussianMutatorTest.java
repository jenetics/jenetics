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

import io.jenetics.distassert.distribution.NormalDistribution;
import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Interval;
import io.jenetics.distassert.observation.Observer;
import io.jenetics.distassert.observation.Sample;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.StableRandomExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.LongStream;

import static io.jenetics.distassert.assertion.Assertions.assertThat;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GaussianMutatorTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new GaussianMutator<>(p);
	}

	@Test
	public void mutate() {
		final var interval = new Interval(0, 10);
		final var scale = interval.size()/2.0;

		final var shape = new GaussianMutator.DistShape(1, 1.0/3.0);
		System.out.println("MEAN: " + shape.mean(interval.min(), interval.max()));

		final DoubleGene gene = DoubleGene.of(interval.min(), interval.max());
		final GaussianMutator<DoubleGene, Double> mutator =
			new GaussianMutator<>(new GaussianMutator.DistShape(1, 1.0/3.0));

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				Sample.repeat(
					100_000,
					sample -> sample.accept(
						mutator
							.mutate(gene, RandomRegistry.random())
							.allele()
					)
				),
				Histogram.Partition.of(interval, 21)
			);

		System.out.println(observation.statistics());
		LongStream
			.of(observation.histogram().buckets().frequencies())
			.forEach(System.out::println);

		assertThat(observation)
			.usingLogger(System.out::println)
			.withinRange(interval)
			.follows(new NormalDistribution(10, 5.0/3.0));
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

