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

import java.util.DoubleSummaryStatistics;
import java.util.random.RandomGenerator;

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

}
