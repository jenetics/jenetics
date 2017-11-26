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
package io.jenetics.ext;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.engine.Limits;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConcatEvolutionPoolTest {

	@Test
	public void concat1() {
		final EvolutionStream<IntegerGene, Integer> stream =
			EvolutionPool.<IntegerGene, Integer>concat()
				.add(streamable(1))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1});
	}

	@Test
	public void concat2() {
		final EvolutionStream<IntegerGene, Integer> stream =
			EvolutionPool.<IntegerGene, Integer>concat()
				.add(streamable(5))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5});
	}

	@Test
	public void concat3() {
		final EvolutionStream<IntegerGene, Integer> stream =
			EvolutionPool.<IntegerGene, Integer>concat()
				.add(streamable(5))
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
			EvolutionPool.<IntegerGene, Integer>concat()
				.add(streamable(3))
				.add(streamable(4))
				.add(streamable(5))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.getGenotypes().get(0).getGene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	}

	@Test
	public void concat5() {
		final EvolutionStream<IntegerGene, Integer> stream =
			EvolutionPool.<IntegerGene, Integer>concat()
				.add(streamable(3))
				.add(streamable(4))
				.add(streamable(5))
				.add(streamable(15))
				.add(streamable(15))
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
			public EvolutionStream<IntegerGene, Integer> stream() {
				throw new UnsupportedOperationException();
			}

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
		};
	}

}
