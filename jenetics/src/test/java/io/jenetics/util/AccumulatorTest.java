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
package io.jenetics.util;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class AccumulatorTest {

	@Test
	public void accumulate() {
		final Accumulator<Integer, ?, ISeq<Integer>> accu = Accumulator.of(ISeq.toISeq());

		final ISeq<ISeq<Integer>> result = IntStream.range(0, 10).boxed()
			.peek(accu)
			.map(i -> accu.result())
			.collect(ISeq.toISeq());

		for (int i = 0; i < result.size(); ++i) {
			final var seq = result.get(i);
			Assert.assertEquals(seq.size(), i + 1);

			for (int j = 0; j < seq.size(); ++j) {
				Assert.assertEquals(seq.get(j).intValue(), j);
			}
		}
	}

	@Test
	public void parallelSynchronizedAccumulate() {
		final Collector<Integer, ?, Long> counting = Collectors.counting();
		final var accu = Accumulator.of(counting).synced();

		IntStream.range(0, 100).boxed().parallel()
			.forEach(accu);

		Assert.assertEquals(accu.result().longValue(), 100);
	}

	@Test
	public void parallelCollect() {
		final Collector<Integer, ?, Long> counting = Collectors.counting();
		final Accumulator<Integer, ?, Long> accu = Accumulator.of(counting);

		final long count = IntStream.range(0, 100).parallel().boxed()
			.collect(accu);
		Assert.assertEquals(count, 100);
	}

	@Test
	public void parallelSynchronizedCollect() {
		final Collector<Integer, ?, Long> counting = Collectors.counting();
		final var accu = Accumulator.of(counting).synced();

		final long count = IntStream.range(0, 100).parallel().boxed()
			.collect(accu);
		Assert.assertEquals(count, 100);
	}

}
