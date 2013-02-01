/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
package org.jenetics.example;

import java.util.function.Function;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Factory;

final class OneCounter
	implements Function<Genotype<BitGene>, Integer>
{
	@Override
	public Integer apply(Genotype<BitGene> genotype) {
		int count = 0;
		for (BitGene gene : genotype.getChromosome()) {
			if (gene.getBit()) {
				++count;
			}
		}
		return count;
	}
}

public class OnesCounting {
	public static void main(String[] args) {
		Factory<Genotype<BitGene>> gtf = Genotype.valueOf(
			new BitChromosome(20, 0.15)
		);
		Function<Genotype<BitGene>, Integer> ff = new OneCounter();
		GeneticAlgorithm<BitGene, Integer> ga =
		new GeneticAlgorithm<>(
			gtf, ff, Optimize.MAXIMUM
		);

		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<BitGene, Integer>()
		);
		ga.setPopulationSize(500);
		ga.setSelectors(
			new RouletteWheelSelector<BitGene, Integer>()
		);
		ga.setAlterers(
			new Mutator<BitGene>(0.55),
			new SinglePointCrossover<BitGene>(0.06)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}

}
