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

import java.util.stream.LongStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Observer;
import io.jenetics.distassert.observation.Sample;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.StableRandomExecutor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GaussianMutatorShapeTest {

	@Test(dataProvider = "parameters")
	public void next(final double shift, final double sigmas) {
		final var shape = new GaussianMutator.DistShape(shift, sigmas);
		final var min = 0.0;
		final var max = 10.0;
		final var stddev = shape.stddev(min, max);
		System.out.println("var=" + stddev*stddev + ", mean=" + shape.mean(min, max));


		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				Sample.repeat(
					1_000_000,
					sample -> sample.accept(shape.next(min, max, RandomRegistry.random()))
				),
				Histogram.Partition.of(min, max, 21)
			);

		System.out.println(observation.statistics());

		//LongStream.of(observation.histogram().buckets().frequencies())
		//	.forEach(System.out::println);

		assertThat(observation)
			.usingLogger(System.out::println)
			.withinRange(min, max)
			.isNormal(shape.mean(min, max), shape.stddev(min, max));
	}

	@DataProvider
	public static Object[][] parameters() {
		return new Object[][] {
			{0.0, 1.0},
			{0.0, 1.5},
			{0.0, 2.0},
			{0.0, 3.0},

			{0.5, 1.0},
			{0.5, 1.5},
			{0.5, 2.0},
			{0.5, 3.0},

			{1.5, 1.0},
			{1.5, 1.5},
			{1.5, 2.0},
			{1.5, 3.0},
		};
	}

}
