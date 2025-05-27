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
package io.jenetics.distassert.assertion;

import static io.jenetics.distassert.assertion.Assurance.assertThatObservation;

import java.util.Random;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.Interval;
import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.RunnableObservation;
import io.jenetics.distassert.observation.Sampling;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class AssuranceTest {

	@Test(dataProvider = "tester")
	public void assertUniformDistribution(
		final HypothesisTester tester,
		final int count
	) {
		final var observation = new RunnableObservation(
			samples -> samples.addAll(new Random(123).doubles(count)),
			Histogram.Partition.of(0, 1, 20)
		);
		observation.run();

		assertThatObservation(observation)
			.usingHypothesisTester(tester)
			.isUniform();
	}

	@Test(dataProvider = "tester")
	public void assertNormalDistribution(
		final HypothesisTester tester,
		final int count
	) {
		final var random = new Random(1234);
		final var interval = new Interval(-10, 10);

		final var observation = new RunnableObservation(
			Sampling.repeat(count, samples ->
				samples.add(random.nextGaussian())
			),
			Histogram.Partition.of(interval, 20)
		);
		observation.run();

		assertThatObservation(observation)
			.usingHypothesisTester(tester)
			.withinRange(interval)
			.isNormal(0, 1);
	}

	@DataProvider
	public Object[][] tester() {
		return new Object[][] {
			{new PearsonsChiSquared(0.5), 1_000_000},
			{new YatesChiSquared(0.05), 1_000},
		};
	}

}
