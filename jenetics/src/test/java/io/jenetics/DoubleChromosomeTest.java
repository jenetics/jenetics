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

import static java.lang.String.format;
import static io.jenetics.distassert.assertion.Assertions.assertThat;
import static io.jenetics.internal.math.DoubleAdder.sum;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.RunnableObservation;
import io.jenetics.distassert.observation.Sampling;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.StableRandomExecutor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DoubleChromosomeTest
	extends NumericChromosomeTester<Double, DoubleGene>
{

	private final DoubleChromosome _factory = DoubleChromosome.of(
		0.0, Double.MAX_VALUE, 500
	);

	@Override
	protected DoubleChromosome factory() {
		return _factory;
	}

	@Test(dataProvider = "seeds")
	public void newInstanceDistribution(final long seed) {
		final double min = 0;
		final double max = 100;

		final var observation = new RunnableObservation(
			Sampling.repeat(1_000, samples ->
				DoubleChromosome.of(min, max, 500).stream()
					.mapToDouble(DoubleGene::doubleValue)
					.forEach(samples::add)
			),
			Histogram.Partition.of(min, max, 20)
		);
		new StableRandomExecutor(seed).execute(observation);

		assertThat(observation).isUniform();
	}

	@DataProvider
	public Object[][] seeds() {
		return new Random(12345678).longs(20)
			.mapToObj(seed -> new Object[]{seed})
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "chromosomes")
	public void chromosomeLength(
		final DoubleChromosome dc,
		final IntRange length
	) {
		Assert.assertTrue(
			dc.length() >= length.min() && dc.length() < length.max(),
			format("Chromosome length %s not in range %s.", dc.length(), length)
		);
	}

	@DataProvider(name = "chromosomes")
	public Object[][] chromosomes() {
		return new Object[][] {
			{DoubleChromosome.of(0, 1), new IntRange(1)},
			{DoubleChromosome.of(new DoubleRange(0, 1)), new IntRange(1)},
			{DoubleChromosome.of(0, 1, 1), new IntRange(1)},
			{DoubleChromosome.of(0, 1, 2), new IntRange(2)},
			{DoubleChromosome.of(0, 1, 20), new IntRange(20)},
			{DoubleChromosome.of(0, 1, new IntRange(2, 10)), new IntRange(2, 10)},
			{DoubleChromosome.of(new DoubleRange(0, 1), new IntRange(2, 10)), new IntRange(2, 10)}
		};
	}

	@Test
	public void doubleStream() {
		final DoubleChromosome chromosome = DoubleChromosome.of(0, 1, 1000);
		final double[] values = chromosome.doubleStream().toArray();

		Assert.assertEquals(values.length, 1000);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(chromosome.get(i).doubleValue(), values[i]);
			Assert.assertEquals(chromosome.doubleValue(i), values[i]);
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofAmbiguousGenes1() {
		DoubleChromosome.of(
			DoubleGene.of(1, 2),
			DoubleGene.of(3, 4),
			DoubleGene.of(5, 6)
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofAmbiguousGenes2() {
		DoubleChromosome.of(
			ISeq.of(
				DoubleGene.of(1, 2),
				DoubleGene.of(3, 4),
				DoubleGene.of(5, 6)
			)
		);
	}

	@Test
	public void map() {
		final var ch1 = DoubleChromosome.of(0, 1, 1000);

		final var ch2 = ch1.map(DoubleChromosomeTest::normalize);

		Assert.assertNotSame(ch2, ch1);
		Assert.assertEquals(ch2.toArray(), normalize(ch1.toArray()));
	}

	static double[] normalize(final double[] values) {
		final double sum = sum(values);
		for (int i = 0; i < values.length; ++i) {
			values[i] /= sum;
		}
		return values;
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void mapNull() {
		final var ch = DoubleChromosome.of(0, 1);
		ch.map(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void mapEmptyArray() {
		final var ch = DoubleChromosome.of(0, 1);
		ch.map(v -> new double[0]);
	}

}
