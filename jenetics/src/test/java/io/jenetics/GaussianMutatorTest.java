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

import static io.jenetics.distassert.assertion.Assertions.assertThat;

import org.testng.annotations.Test;

import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Interval;
import io.jenetics.distassert.observation.Observer;
import io.jenetics.distassert.observation.Sample;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.StableRandomExecutor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GaussianMutatorTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new GaussianMutator<>(p);
	}

	@Test
	public void shapeNext() {
		final var shape = new GaussianMutator.Shape(0, 2.0);
	}

	@Test
	public void mutate() {
		final var interval = new Interval(-10, 10);
		final var range = new DoubleRange(interval.min(), interval.max());
		final var shape = new GaussianMutator.Shape(0, 1.0);
		final var stddev = shape.stddev(range);
		final var mean = shape.mean(range);

		final var gene = DoubleGene.of(interval.min(), interval.max());
		final var mutator = new GaussianMutator<DoubleGene, Double>(shape);

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				Sample.repeat(
					100_000,
					sample -> sample.accept(
						shape.sample(RandomRegistry.random(), range)
						/*
						mutator
							.mutate(gene, RandomRegistry.random())
							.allele()
						 */
					)
				),
				Histogram.Partition.of(interval, 21)
			);

		System.out.println(observation.statistics());

		assertThat(observation)
			.usingLogger(System.out::println)
			.withinRange(interval)
			.isNormal(mean, stddev);
	}

//	@Test
//	public void mutateValidGene() {
//		final var mutator = new GaussianMutator<DoubleGene, Double>() {
//			public DoubleGene mutate(
//				final DoubleGene gene,
//				final RandomGenerator random
//			) {
//				return super.mutate(gene, random);
//			}
//		};
//
//		final var random = new Random() {
//			@Override
//			public double nextGaussian(double mean, double stddev) {
//				return 1;
//			}
//		};
//
//		DoubleGene gene = DoubleGene.of(0.9, 0, 1);
//		Assert.assertTrue(gene.isValid());
//
//		gene = mutator.mutate(gene, random);
//		Assert.assertTrue(gene.isValid());
//		Assert.assertEquals(gene.doubleValue(), Math.nextDown(1.0));
//	}

//	@Test
//	public void mutateInvalidGene() {
//		final var mutator = new GaussianMutator<DoubleGene, Double>() {
//			public DoubleGene mutate(
//				final DoubleGene gene,
//				final RandomGenerator random
//			) {
//				return super.mutate(gene, random);
//			}
//		};
//
//		final DoubleGene gene = DoubleGene.of(0.9, 5, 1);
//		Assert.assertFalse(gene.isValid());
//
//		final DoubleGene gene1 = mutator.mutate(gene, new Random());
//		Assert.assertSame(gene1, gene);
//	}

}

