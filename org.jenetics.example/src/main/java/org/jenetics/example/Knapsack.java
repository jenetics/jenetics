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

import static org.jenetics.util.math.random.nextDouble;

import java.util.Random;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

final class Item {
	public final double size;
	public final double value;

	Item(final double size, final double value) {
		this.size = size;
		this.value = value;
	}
}

final class KnapsackFunction
	implements Function<Genotype<BitGene>, Double>
{
	private final Item[] items;
	private final double size;

	public KnapsackFunction(final Item[] items, double size) {
		this.items = items;
		this.size = size;
	}

	@Override
	public Double apply(final Genotype<BitGene> genotype) {
		final BitChromosome ch = 
				(BitChromosome)genotype.getChromosome();
		double size = 0;
		double value = 0;
		for (int i = 0, n = ch.length(); i < n; ++i) {
			if (ch.get(i)) {
				size += items[i].size;
				value += items[i].value;
			}
		}

		return size <= this.size ? value : 0;
	}
}

public class Knapsack {

	private static KnapsackFunction FF(final int n, final double size) {
		final Item[] items = new Item[n];
		try (Scoped<? extends Random> random =
			RandomRegistry.scope(new LCG64ShiftRandom(123)))
		{
			for (int i = 0; i < items.length; ++i) {
				items[i] = new Item(
					nextDouble(random.get(), 0, 100),
					nextDouble(random.get(), 0, 100)
				);
			}
		}

		return new KnapsackFunction(items, size);
	}

	public static void main(String[] args) throws Exception {
		final int nitems = 15;
		final double kssize = nitems*100.0/3.0;

		final KnapsackFunction ff = FF(nitems, kssize);
		final Factory<Genotype<BitGene>> genotype = Genotype.of(
			BitChromosome.of(nitems, 0.5)
		);

		final GeneticAlgorithm<BitGene, Double> ga = new GeneticAlgorithm<>(
			genotype, ff
		);
		ga.setPopulationSize(500);
		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<BitGene, Double>()
		);
		ga.setSurvivorSelector(
			new TournamentSelector<BitGene, Double>(5)
		);
		ga.setOffspringSelector(
			new RouletteWheelSelector<BitGene, Double>()
		);
		ga.setAlterers(
			 new Mutator<BitGene>(0.115),
			 new SinglePointCrossover<BitGene>(0.16)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}
}
