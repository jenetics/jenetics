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
package org.jenetics.engine;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.DoubleGene;
import org.jenetics.Optimize;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ExecutionTimeLimitTest {

	private static final class CountClock extends Clock {
		int count = 0;

		@Override
		public Instant instant() {
			return Instant.ofEpochMilli(++count);
		}

		@Override public ZoneId getZone() { return null; }
		@Override public Clock withZone(ZoneId zone) { return null; }
	}

	@Test(dataProvider = "durations")
	public void test(final Integer millis) {
		final CountClock clock = new CountClock();
		final Duration duration = Duration.ofMillis(millis);

		final AtomicInteger count = new AtomicInteger();
		stream().limit(limit.byExecutionTime(duration, clock))
			.forEach(s -> count.incrementAndGet());

		Assert.assertEquals(count.get(), Math.max(millis.intValue(), 1));
		Assert.assertEquals(clock.count, count.get() + 1);
	}

	private static EvolutionStream<DoubleGene, Double> stream() {
		final Function<EvolutionStart<DoubleGene, Double>, EvolutionResult<DoubleGene, Double>> ff =
			s -> EvolutionResult.of(
				Optimize.MAXIMUM,
				new Population<DoubleGene, Double>(),
				1, EvolutionDurations.ZERO, 0, 0, 0
			);

		return new EvolutionStreamImpl<DoubleGene, Double>(
			() -> EvolutionStart.of(new Population<DoubleGene, Double>(), 1),
			ff
		);
	}

	@DataProvider(name = "durations")
	public Object[][] durations() {
		return new Object[][] {
			{0}, {1}, {2}, {3}, {7}, {11}, {17}, {100}
		};
	}

}
