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

import nl.jqno.equalsverifier.EqualsVerifier;

import java.util.LongSummaryStatistics;
import java.util.Random;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

import io.jenetics.util.Factory;
import io.jenetics.util.ObjectTester;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class LongSummaryTest extends ObjectTester<LongSummary> {

	@Override
	protected Factory<LongSummary> factory() {
		return () -> {
			final Random random = RandomRegistry.random();

			final LongSummaryStatistics statistics = new LongSummaryStatistics();
			IntStream.range(0, 100)
				.map(i -> random.nextInt(1_000_000))
				.forEach(statistics);

			return LongSummary.of(statistics);
		};
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(LongSummary.class).verify();
	}

}
