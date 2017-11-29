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

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.engine.Limits;
import io.jenetics.util.ISeq;

import io.jenetics.ext.EvolutionStreams;
import io.jenetics.ext.engine.ConcatEnginePool;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConcatEnginePoolTest {

	@Test
	public void concat0() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.<IntegerGene, Integer>of().stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{});
	}

	@Test
	public void concat1() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(streamable(1))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1});
	}

	@Test
	public void concat1a() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(streamable(1))
				.stream(() -> EvolutionStreams.result(5).toEvolutionStart());

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{6});
	}

	@Test
	public void concat1b() {
		EvolutionInit<IntegerGene> init = EvolutionInit.of(
			EvolutionStreams.result(5)
				.toEvolutionStart()
				.getPopulation().stream()
				.map(Phenotype::getGenotype)
				.collect(ISeq.toISeq()),
			1
		);

		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(streamable(1))
				.stream(init);

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{6});
	}

	@Test
	public void concat2() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(streamable(5))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5});
	}

	@Test
	public void concat3() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(streamable(5))
				.stream()
				.limit(Limits.byFixedGeneration(3));

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3});
	}

	@Test
	public void concat4() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(
				streamable(3),
				streamable(4),
				streamable(5)
			)
			.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	}

	@Test
	public void concat5() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEnginePool.of(
				streamable(3),
				streamable(4),
				streamable(5),
				streamable(15),
				streamable(15)
			)
			.stream()
			.limit(Limits.byFixedGeneration(15));

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
	}

	static EvolutionStreamable<IntegerGene, Integer> streamable(final int size) {
		return new EvolutionStreamable<IntegerGene, Integer>() {
			@Override
			public EvolutionStream<IntegerGene, Integer>
			stream(final Supplier<EvolutionStart<IntegerGene, Integer>> start) {
				return EvolutionStreams.stream(
					Stream.generate(new Supplier<Integer>() {
						Integer value = null;

						@Override
						public Integer get() {
							if (value == null) {
								value  = start.get().getPopulation().isEmpty()
									? 0
									: start.get().getPopulation()
									.get(0).getGenotype().getGene().intValue();
							}
							value += 1;

							return value;
						}
					}).limit(size)
				);
			}

			@Override
			public EvolutionStream<IntegerGene, Integer>
			stream(final EvolutionInit<IntegerGene> init) {
				return EvolutionStreams.stream(
					Stream.generate(new Supplier<Integer>() {
						Integer value = null;

						@Override
						public Integer get() {
							if (value == null) {
								value  = init.getPopulation().get(0).getGene().intValue();
							}
							value += 1;

							return value;
						}
					}).limit(size)
				);
			}
		};
	}

}
