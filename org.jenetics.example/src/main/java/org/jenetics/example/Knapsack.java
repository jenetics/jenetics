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

import org.jscience.mathematics.number.Float64;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Chromosome;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

final class Item {
	public double size;
	public double value;
}

final class KnapsackFunction
	implements Function<Genotype<BitGene>, Float64>
{
	private final Item[] _items;
	private final double _size;

	public KnapsackFunction(final Item[] items, double size) {
		_items = items;
		_size = size;
	}

	public Item[] getItems() {
		return _items;
	}

	@Override
	public Float64 apply(final Genotype<BitGene> genotype) {
		final Chromosome<BitGene> ch = genotype.getChromosome();

		double size = 0;
		double value = 0;
		for (int i = 0, n = ch.length(); i < n; ++i) {
			if (ch.getGene(i).getBit()) {
				size += _items[i].size;
				value += _items[i].value;
			}
		}

		Float64 result = Float64.ZERO;
		if (size <= _size) {
			result = Float64.valueOf(value);
		}
		return result;
	}
}

public class Knapsack {

	private static KnapsackFunction FF(int n, double size) {
		Item[] items = new Item[n];
		for (int i = 0; i < items.length; ++i) {
			items[i] = new Item();
			items[i].size = (Math.random() + 1)*10;
			items[i].value = (Math.random() + 1)*15;
		}

		return new KnapsackFunction(items, size);
	}

	public static void main(String[] argv) throws Exception {
		KnapsackFunction ff = FF(15, 100);
		Factory<Genotype<BitGene>> genotype = Genotype.valueOf(
			new BitChromosome(15, 0.5)
		);

		GeneticAlgorithm<BitGene, Float64> ga =
		new GeneticAlgorithm<>(
			genotype, ff
		);
		ga.setPopulationSize(500);
		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<BitGene, Float64>()
		);
		ga.setSurvivorSelector(
			new TournamentSelector<BitGene, Float64>(5)
		);
		ga.setOffspringSelector(
			new RouletteWheelSelector<BitGene, Float64>()
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
