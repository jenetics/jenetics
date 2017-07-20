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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.stat;

import java.util.IntSummaryStatistics;
import java.util.Random;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class IntSummaryTest extends ObjectTester<IntSummary> {

	@Override
	protected Factory<IntSummary> factory() {
		return () -> {
			final Random random = RandomRegistry.getRandom();

			final IntSummaryStatistics statistics = new IntSummaryStatistics();
			IntStream.range(0, 100)
				.map(i -> random.nextInt(1_000_000))
				.forEach(statistics);

			return IntSummary.of(statistics);
		};
	}

}
