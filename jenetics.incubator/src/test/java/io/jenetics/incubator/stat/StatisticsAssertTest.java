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
package io.jenetics.incubator.stat;

import static io.jenetics.incubator.stat.Assurance.assertThatObservation;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StatisticsAssertTest {

	@Test
	public void assertUniformDistribution() {
		final var observation = new RunnableObservation(
			samples -> samples.addAll(new Random(123).doubles(100_000))
			,
			Histogram.Partition.of(0, 1, 20)
		);
		observation.run();

		assertThatObservation(observation)
			.usingHypothesisTester(new PearsonsChiSquared(0.0005))
			.isUniform();
	}

	//@Test
	public void assertNormalDistribution() {
		final var hist = Histogram.Builder.of(new Interval(-2, 2), 10);

		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 100_000; ++i) {
			hist.add(random.nextGaussian(0, 1));
		}

		assertThatObservation(hist.build()).isNormal(0, 1);
	}

}
