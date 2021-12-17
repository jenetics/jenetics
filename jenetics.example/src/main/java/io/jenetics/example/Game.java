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
package io.jenetics.example;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 3.8
 */
public class Game {

	record Player(double value) implements Comparable<Player> {
		@Override
		public int compareTo(final Player other) {
			return Double.compare(value, other.value);
		}
	}

	public static void main(final String[] args) {

		final Codec<Player, DoubleGene> codec = Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new Player(gt.gene().doubleValue())
		);

		final AtomicReference<ISeq<Phenotype<DoubleGene, Double>>>
			population = new AtomicReference<>();

		// Fitness function chooses the second individual randomly from the
		// current population. The population is set by the stream as side-
		// effect.
		final Function<Player, Double> fitness = player -> {
			final Seq<Phenotype<DoubleGene, Double>> pop = population.get();

			final Player other;
			if (pop != null) {
				final int index = RandomRegistry.random().nextInt(pop.size());
				other = codec.decode(pop.get(index).genotype());
			} else {
				other = new Player(0.5);
			}

			return (double)player.compareTo(other);
		};

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitness, codec)
			.build();

		final Player best = codec.decode(
			engine.stream()
				.limit(Limits.bySteadyFitness(50))
				.peek(er -> population.set(er.population()))
				.collect(EvolutionResult.toBestGenotype())
		);

		System.out.println(best.value);
	}

}
