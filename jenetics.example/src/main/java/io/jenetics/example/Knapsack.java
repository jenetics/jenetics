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

import static java.util.Objects.requireNonNull;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collector;
import java.util.stream.Stream;

import io.jenetics.BitGene;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Problem;
import io.jenetics.example.Knapsack.Item;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

/**
 * <i>Canonical</i> definition of the <i>Knapsack</i> problem. This
 * <i>reference</i> implementation is used for (evolution) performance tests of
 * the GA {@link io.jenetics.engine.Engine}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public final class Knapsack implements Problem<ISeq<Item>, BitGene, Double> {

	/**
	 * This class represents a knapsack item with the specific <i>size</i> and
	 * <i>value</i>.
	 */
	public record Item(double size, double value)
		implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 1L;

		/**
		 * @param size the item size
		 * @param value the item value
		 */
		public Item {
			Requires.nonNegative(size);
			Requires.nonNegative(value);
		}

		/**
		 * Create a new <i>random</i> knapsack item for testing purpose.
		 *
		 * @param random the random engine used for creating the knapsack item
		 * @return a new <i>random</i> knapsack item
		 * @throws NullPointerException if the random engine is {@code null}
		 */
		public static Item random(final RandomGenerator random) {
			return new Item(random.nextDouble()*100, random.nextDouble()*100);
		}

		/**
		 * Return a {@link Collector}, which sums the size and value of knapsack
		 * items.
		 *
		 * @return a knapsack item sum {@link Collector}
		 */
		public static Collector<Item, ?, Item> toSum() {
			return Collector.of(
				() -> new double[2],
				(a, b) -> {a[0] += b.size; a[1] += b.value;},
				(a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
				r -> new Item(r[0], r[1])
			);
		}
	}


	private final Codec<ISeq<Item>, BitGene> _codec;
	private final double _knapsackSize;

	/**
	 * Create a new {@code Knapsack} definition with the given
	 *
	 * @param items the basic {@link Set} of knapsack items.
	 * @param knapsackSize the maximal knapsack size
	 * @throws NullPointerException if the {@code items} set is {@code null}
	 */
	public Knapsack(final ISeq<Item> items, final double knapsackSize) {
		_codec = Codecs.ofSubSet(items);
		_knapsackSize = knapsackSize;
	}

	@Override
	public Function<ISeq<Item>, Double> fitness() {
		return items -> {
			final Item sum = items.stream().collect(Item.toSum());
			return sum.size <= _knapsackSize ? sum.value : 0;
		};
	}

	@Override
	public Codec<ISeq<Item>, BitGene> codec() {
		return _codec;
	}

	/**
	 * Factory method for creating <i>same</i> Knapsack problems for testing
	 * purpose.
	 *
	 * @param itemCount the number of knapsack items in the basic set
	 * @param random the random engine used for creating random knapsack items.
	 *        This allows to create reproducible item sets and reproducible
	 *        {@code Knapsack} problems, respectively.
	 * @return a {@code Knapsack} problem object (for testing purpose).
	 */
	public static Knapsack of(final int itemCount, final RandomGenerator random) {
		requireNonNull(random);

		return new Knapsack(
			Stream.generate(() -> Item.random(random))
				.limit(itemCount)
				.collect(ISeq.toISeq()),
			itemCount*100.0/3.0
		);
	}

	public static void main(final String[] args) {
		final Knapsack knapsack = Knapsack.of(15, new Random(123));

		// Configure and build the evolution engine.
		final Engine<BitGene, Double> engine = Engine.builder(knapsack)
			.populationSize(500)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<BitGene, Double> best = engine.stream()
			// Truncate the evolution stream after 7 "steady"
			// generations.
			.limit(bySteadyFitness(7))
			// The evolution will stop after maximal 100
			// generations.
			.limit(100)
			// Update the evaluation statistics after
			// each generation
			.peek(statistics)
			// Collect (reduce) the evolution stream to
			// its best phenotype.
			.collect(toBestPhenotype());

		System.out.println(statistics);
		System.out.println(best);
	}

}
