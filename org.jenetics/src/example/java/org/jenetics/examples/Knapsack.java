/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.examples;

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

		if (size > _size) {
			return Float64.ZERO;
		} else {
			return Float64.valueOf(value);
		}
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
