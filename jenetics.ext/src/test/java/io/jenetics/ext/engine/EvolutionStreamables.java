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

import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;

import io.jenetics.ext.EvolutionStreams;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class EvolutionStreamables {
	private EvolutionStreamables() {
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
									: start.get()
										.getPopulation()
										.get(0)
										.getGenotype()
										.getGene()
										.intValue();
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
								value  = init.getPopulation()
									.get(0)
									.getGene()
									.intValue();
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
