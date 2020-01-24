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
package io.jenetics.engine;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.Optimize;
import io.jenetics.internal.engine.EvolutionStreamImpl;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
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
		stream()
			.limit(Limits.byExecutionTime(duration, clock))
			.forEach(s -> count.incrementAndGet());

		Assert.assertEquals(count.get(), millis.intValue());
		Assert.assertEquals(clock.count, count.get() + 1);
	}

	private static EvolutionStream<DoubleGene, Double> stream() {
		final Evolution<DoubleGene, Double> evolution =
			s -> EvolutionResult.of(
				Optimize.MAXIMUM,
				ISeq.empty(),
				1, EvolutionDurations.ZERO, 0, 0, 0
			);

		return new EvolutionStreamImpl<DoubleGene, Double>(
			() -> EvolutionStart.of(ISeq.empty(), 1),
			evolution
		);
	}

	@DataProvider(name = "durations")
	public Object[][] durations() {
		return new Object[][] {
			{0}, {1}, {2}, {3}, {7}, {11}, {17}, {100}
		};
	}

}
