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

import org.testng.annotations.Test;

import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConcatStreamablePoolTest {

	@Test
	public void concat() {
		final EvolutionStream<IntegerGene, Integer> stream =
			EvolutionStreamablePool.<IntegerGene, Integer>concat()
				.add(streamable(3))
				.add(streamable(3))
				.add(streamable(3))
				.stream();

		stream
			.map(r -> r.getGenotypes())
			.forEach(System.out::println);
	}

	private EvolutionStreamable<IntegerGene, Integer> streamable(final int size) {
		return new EvolutionStreamable<IntegerGene, Integer>() {
			@Override
			public EvolutionStream<IntegerGene, Integer> stream() {
				return null;
			}

			@Override
			public EvolutionStream<IntegerGene, Integer>
			stream(final Supplier<EvolutionStart<IntegerGene, Integer>> start) {
				final int begin = start.get().getPopulation().isEmpty()
					? 1
					: start.get().getPopulation()
						.get(0).getGenotype().getGene().intValue();
				final int end = begin + size;

				return EvolutionStreams.stream(
					IntRange.of(begin, end).stream().boxed()
				);
			}
		};
	}

}
