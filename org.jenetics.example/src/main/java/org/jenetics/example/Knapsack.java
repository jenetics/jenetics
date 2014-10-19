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

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.internal.math.random.nextDouble;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Engine;
import org.jenetics.util.Factory;

/**
 * This class represents a knapsack item, with a specific "size" and "value".
 */
final class Item {
	public final double size;
	public final double value;

	Item(final double size, final double value) {
		this.size = size;
		this.value = value;
	}

	/**
	 * Create a new random knapsack item.
	 */
	static Item of(final Random r) {
		return new Item(nextDouble(r, 0, 100), nextDouble(r, 0, 100));
	}

	/**
	 * Create a new collector for summing up the knapsack items.
	 */
	static Collector<Item, ?, Item> toSum() {
		return Collector.of(
			() -> new double[2],
			(a, b) -> {a[0] += b.size; a[1] += b.value;},
			(a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
			r -> new Item(r[0], r[1])
		);
	}
}

/**
 * The knapsack fitness function class, which is parametrized with the available
 * items and the size of the knapsack.
 */
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
		final Item sum = ((BitChromosome)genotype.getChromosome()).ones()
			.mapToObj(i -> items[i])
			.collect(Item.toSum());

		return sum.size <= this.size ? sum.value : 0;
	}
}

/**
 * The main class.
 */
public class Knapsack {

	public static void main(String[] args) throws Exception {
		final int nitems = 15;
		final double kssize = nitems*100.0/3.0;

		final KnapsackFunction ff = new KnapsackFunction(
				IntStream.range(0, nitems)
					.mapToObj(i -> Item.of(new Random(123)))
					.toArray(Item[]::new),
				kssize
			);

		final Factory<Genotype<BitGene>> gtf = Genotype.of(
			BitChromosome.of(nitems, 0.5)
		);

		final Engine<BitGene, Double> engine = Engine.builder(ff, gtf)
			.populationSize(500)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.build();

		final Phenotype<BitGene, Double> result = engine.stream().limit(100)
			.collect(toBestPhenotype());

		System.out.println(result);
	}
}
