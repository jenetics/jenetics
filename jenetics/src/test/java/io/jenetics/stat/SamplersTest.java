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
package io.jenetics.stat;

import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.distassert.assertion.Assertions.assertThat;

import java.util.random.RandomGenerator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.distribution.LinearDistribution;
import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Interval;
import io.jenetics.distassert.observation.Observer;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.StableRandomExecutor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SamplersTest {

	@Test
	public void linearMeanOneHalf() {
		final var interval = new Interval(0, 10);
		final var range = new DoubleRange(interval.min(), interval.max());
		final var sampler = Samplers.linear(0.5);

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				consumer -> {
					final var random = RandomRegistry.random();
					for (int i = 0; i < 1_000_000; ++i) {
						consumer.accept(sampler.sample(random, range));
					}
				},
				Histogram.Partition.of(interval, 20)
			);

		assertThat(observation).isUniform();
	}

	@Test
	public void linearMeanSqrtTwoHalf() {
		final var interval = new Interval(0, 10);
		final var range = new DoubleRange(interval.min(), interval.max());
		final var sampler = Samplers.linear(Math.sqrt(2)/2.0);

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				consumer -> {
					final var random = RandomRegistry.random();
					for (int i = 0; i < 1_000_000; ++i) {
						consumer.accept(sampler.sample(random, range));
					}
				},
				Histogram.Partition.of(interval, 20)
			);

		assertThat(observation)
			.follows(new LinearDistribution(interval, 0));
	}

	@Test
	public void linearOneMeanMinusSqrtTwoHalf() {
		final var interval = new Interval(0, 10);
		final var range = new DoubleRange(interval.min(), interval.max());
		final var sampler = Samplers.linear(1 - Math.sqrt(2)/2.0);

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				consumer -> {
					final var random = RandomRegistry.random();
					for (int i = 0; i < 1_000_000; ++i) {
						consumer.accept(sampler.sample(random, range));
					}
				},
				Histogram.Partition.of(interval, 20)
			);

		assertThat(observation)
			.follows(new LinearDistribution(interval, 2.0/interval.size()));
	}

	@Test(dataProvider = "ranges")
	public void linearBoundaryMinValue(final DoubleRange range) {
		final var random = RandomGenerator.getDefault();
		final var sampler = Samplers.linear(0.0);

		assertThat(sampler.sample(random, range))
			.isEqualTo(range.min());
	}

	@Test(dataProvider = "ranges")
	public void linearBoundaryMaxValue(final DoubleRange range) {
		final var random = RandomGenerator.getDefault();
		final var sampler = Samplers.linear(Math.nextDown(1.0));

		assertThat(sampler.sample(random, range))
			.isEqualTo(Math.nextDown(range.max()));
	}

	@Test(dataProvider = "ranges")
	public void linearSamples(final DoubleRange range) {
		final var random = RandomGenerator.getDefault();

		double previous = 0;
		for (int i = 0; i < 100_000; ++i) {
			final var sampler = Samplers.linear(random.nextDouble());
			final var sample = sampler.sample(random, range);

			assertThat(sample).isNotEqualTo(previous);

			assertThat(sample)
				.isGreaterThanOrEqualTo(range.min())
				.isLessThan(range.max());

			previous = sample;
		}
	}

	@DataProvider
	public Object[][] ranges() {
		return new Object[][] {
			{new DoubleRange(0, 1)},
			{new DoubleRange(1, 2)},
			{new DoubleRange(1, 22)},
			{new DoubleRange(10, 23)},
			{new DoubleRange(-1, 2)},
			{new DoubleRange(-110, -2)},
			{new DoubleRange(-11, -5)}
		};
	}

	@Test(dataProvider = "gaussianParameters")
	public void gaussian(double mean, double stddev) {
		final var interval = new Interval(0, 10);
		final var range = new DoubleRange(interval.min(), interval.max());
		final var sampler = Samplers.gaussian(mean, stddev);

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				consumer -> {
					final var random = RandomRegistry.random();
					for (int i = 0; i < 1_000_000; ++i) {
						consumer.accept(sampler.sample(random, range));
					}
				},
				Histogram.Partition.of(interval, 20)
			);

		assertThat(observation)
			.withinRange(interval)
			.isNormal(mean, stddev);
	}

	@DataProvider
	public static Object[][] gaussianParameters() {
		return new Object[][] {
			{5.0, 2.0},
			{2.0, 1.0},
			{7.0, 5.0}
		};
	}

}
