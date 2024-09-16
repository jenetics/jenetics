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

import static io.jenetics.testfixtures.stat.StatisticsAssert.assertThatObservation;

import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import io.jenetics.testfixtures.stat.Histogram;
import io.jenetics.testfixtures.stat.PearsonChi2Tester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StatisticsAssertTest {

	@Test
	public void assertUniformDistribution() {
		final var hist = Histogram.Builder.of(0, 1, 20);

		final var random = RandomGenerator.getDefault();
		random.doubles(10_000).forEach(hist);

		assertThatObservation(hist.build()).isUniform();
		assertThatObservation(hist.build())
			.withTester(new PearsonChi2Tester(0.0005))
			.isUniform();
	}

	@Test
	public void assertNormalDistribution() {
		final var hist = Histogram.Builder.of(-2, 2, 10);

		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 100_000; ++i) {
			hist.accept(random.nextGaussian(0, 1));
		}

		assertThatObservation(hist.build()).isNormal(0, 1);
	}

}
