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
package io.jenetics.internal.engine;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EvolutionStreamImplTest {

	@Test
	public void limit() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(
				gt -> gt.getGene().getAllele(),
				DoubleChromosome.of(0, 1))
			.build();

		final AtomicLong count = new AtomicLong();
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(new CountLimit(1000))
			.limit(new CountLimit(100))
			.limit(new CountLimit(10))
			.limit(new CountLimit(100))
			.peek(r -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.get(), 10L);
		Assert.assertEquals(result.getTotalGenerations(), 10L);
	}

	@Test
	public void limit0() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(
				gt -> gt.getGene().getAllele(),
				DoubleChromosome.of(0, 1))
			.build();

		final AtomicLong count = new AtomicLong();
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(new CountLimit(0))
			.peek(r -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.get(), 0L);
		Assert.assertNull(result);
	}

	@Test
	public void limit1() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(
				gt -> gt.getGene().getAllele(),
				DoubleChromosome.of(0, 1))
			.build();

		final AtomicLong count = new AtomicLong();
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(new CountLimit(1))
			.peek(r -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.get(), 1L);
		Assert.assertEquals(result.getTotalGenerations(), 1L);
	}

	private static final class CountLimit implements Predicate<Object> {
		private final long _limit;
		private long _count = 0;

		CountLimit(final long limit) {
			_limit = limit;
		}

		@Override
		public boolean test(final Object o) {
			return _limit >= ++_count;
		}
	}

	@Test
	public void spliterator() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(
				gt -> gt.getGene().getAllele(),
				DoubleChromosome.of(0, 1))
			.build();

		final EvolutionStream<DoubleGene, Double> stream = engine.stream()
			.limit(Limits.byFixedGeneration(10));

		final Spliterator<EvolutionResult<DoubleGene, Double>>
			spliterator = stream.spliterator();

		final long count = StreamSupport.stream(spliterator, false)
			.count();

		Assert.assertEquals(count, 10);
	}

}
