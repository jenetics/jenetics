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
package io.jenetics.ext.engine;

import java.util.concurrent.atomic.AtomicLong;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.internal.math.DoubleAdder;
import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EnginePoolTest {

	private static final Problem<double[], DoubleGene, Double> PROBLEM =
		Problem.of(
			DoubleAdder::sum,
			Codecs.ofVector(new DoubleRange(0, 10), 5)
		);

	private final Engine.Builder<DoubleGene, Double> _builder = Engine.builder(PROBLEM)
		.maximizing()
		.selector(new RouletteWheelSelector<>())
		.alterers(
			new Mutator<>(0.2),
			new MeanAlterer<>()
		);

	private final Engine<DoubleGene, Double> _engine1 = _builder.build();

	private final Engine<DoubleGene, Double> _engine2 = _builder
		.alterers(
			new Mutator<>(0.1),
			new MeanAlterer<>())
		.build();

	private final Engine<DoubleGene, Double> _engine3 = _builder
		.alterers(
			new Mutator<>(0.01),
			new MeanAlterer<>())
		.build();

	@Test
	public void concat1() {
		final EvolutionStreamable<DoubleGene, Double> engine =
			ConcatEngine.of(
				_engine1.limit(10),
				_engine2.limit(10),
				_engine3.limit(10)
			);

		final AtomicLong count = new AtomicLong(0);
		final EvolutionResult<DoubleGene, Double> result =
			engine.stream()
				.limit(Limits.byFixedGeneration(40))
				.limit(100)
				.peek(r -> count.incrementAndGet())
				.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.intValue(), 30);
		Assert.assertEquals(result.totalGenerations(), 30);
	}

	@Test
	public void concat2() {
		final EvolutionStreamable<DoubleGene, Double> engine =
			ConcatEngine.of(
				_engine1.limit(10),
				_engine2.limit(10),
				_engine3.limit(10)
			);

		final AtomicLong count = new AtomicLong(0);
		final EvolutionResult<DoubleGene, Double> result =
			engine.stream()
				.limit(Limits.byFixedGeneration(15))
				.limit(100)
				.peek(r -> count.incrementAndGet())
				.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.intValue(), 15);
		Assert.assertEquals(result.totalGenerations(), 15);
	}

	@Test
	public void concat3() {
		final EvolutionStreamable<DoubleGene, Double> engine =
			ConcatEngine.of(
				_engine1.limit(10),
				_engine2.limit(10),
				_engine3.limit(10)
			);

		final AtomicLong count = new AtomicLong(0);
		final EvolutionResult<DoubleGene, Double> result =
			engine.stream()
				.limit(Limits.byFixedGeneration(15))
				.limit(9)
				.peek(r -> count.incrementAndGet())
				.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.intValue(), 9);
		Assert.assertEquals(result.totalGenerations(), 9);
	}

	@Test
	public void cycle1() {
		final EvolutionStreamable<DoubleGene, Double> engine =
			CyclicEngine.of(
				_engine1.limit(10),
				_engine2.limit(10),
				_engine3.limit(10)
			);

		final AtomicLong count = new AtomicLong(0);
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(100)
			.peek(r -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.intValue(), 100);
		Assert.assertEquals(result.totalGenerations(), 100);
	}

	@Test
	public void cycle2() {
		final EvolutionStreamable<DoubleGene, Double> engine =
			CyclicEngine.of(
				_engine1.limit(10),
				_engine2.limit(10),
				_engine3.limit(10)
			);

		final AtomicLong count = new AtomicLong(0);
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(Limits.byFixedGeneration(50))
			.limit(100)
			.peek(r -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.intValue(), 50);
		Assert.assertEquals(result.totalGenerations(), 50);
	}

	@Test
	public void cycle3() {
		final EvolutionStreamable<DoubleGene, Double> engine =
			CyclicEngine.of(
				_engine1.limit(10),
				_engine2.limit(10),
				_engine3.limit(10)
			);

		final AtomicLong count = new AtomicLong(0);
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(Limits.byFixedGeneration(50))
			.limit(15)
			.peek(r -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.intValue(), 15);
		Assert.assertEquals(result.totalGenerations(), 15);
	}

}
