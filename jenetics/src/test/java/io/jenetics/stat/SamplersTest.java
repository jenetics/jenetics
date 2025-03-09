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

import java.util.DoubleSummaryStatistics;
import java.util.random.RandomGenerator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SamplersTest {

	@Test
	public void linear() {
		final var dist = Samplers.linear(0.12);
		final var random = RandomGenerator.getDefault();
		final var stat = new DoubleSummaryStatistics();

		final var range = DoubleRange.of(10, 100);
		for (int i = 0; i < 100000; ++i) {
			final var value = dist.sample(random, range);
			stat.accept(value);
		}

		//System.out.println(stat);
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
			{DoubleRange.of(0, 1)},
			{DoubleRange.of(1, 2)},
			{DoubleRange.of(1, 22)},
			{DoubleRange.of(10, 23)},
			{DoubleRange.of(-1, 2)},
			{DoubleRange.of(-110, -2)},
			{DoubleRange.of(-11, -5)}
		};
	}

}
