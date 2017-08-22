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
package org.jenetics.example;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.Population;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 3.8
 */
public class Game {

	static final class Player implements Comparable<Player> {
		final double value;

		private Player(final double value) {
			this.value = value;
		}

		@Override
		public int compareTo(final Player other) {
			return Double.compare(value, other.value);
		}

		static Player of(final double value) {
			return new Player(value);
		}
	}

	public static void main(final String[] args) {

		final Codec<Player, DoubleGene> codec = Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> Player.of(gt.getGene().doubleValue())
		);

		final AtomicReference<Population<DoubleGene, Double>>
			population = new AtomicReference<>();

		// Fitness function chooses the second individual randomly from the
		// current population. The population is set by the stream as side-
		// effect.
		final Function<Player, Double> fitness = player -> {
			final Population<DoubleGene, Double> pop = population.get();

			final Player other;
			if (pop != null) {
				final int index = RandomRegistry.getRandom().nextInt(pop.size());
				other = codec.decode(pop.get(index).getGenotype());
			} else {
				other = Player.of(0.5);
			}

			return (double)player.compareTo(other);
		};

		final Engine<DoubleGene, Double> engine = Engine
			.builder(fitness, codec)
			.build();

		final Player best = codec.decode(
			engine.stream()
				.limit(limit.bySteadyFitness(50))
				.peek(er -> population.set(er.getPopulation()))
				.collect(EvolutionResult.toBestGenotype())
		);

		System.out.println(best.value);
	}

}
