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
package org.jenetics.optimizer;

import static org.jenetics.engine.limit.byFixedGeneration;
import static org.jenetics.engine.limit.bySteadyFitness;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.util.IntRange;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamOptimizerTest {

	public static void main(final String[] args) {
		final int nitems = 200;
		final double kssize = nitems*100.0/3.0;

		final FF_1 fitness =
			RandomRegistry.with(new LCG64ShiftRandom(1234), r ->
					new FF_1(
						Stream.generate(Item_1::random)
							.limit(nitems)
							.toArray(Item_1[]::new),
						kssize
					)
			);

		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
		final Codec<Genotype<BitGene>, BitGene> codec = Codec.of(
			Genotype.of(BitChromosome.of(nitems, 0.5)),
			Function.<Genotype<BitGene>>identity()
		);

		final EvolutionParamOptimizer<Genotype<BitGene>, BitGene, Double> optimizer =
			new EvolutionParamOptimizer<>(fitness, codec, () -> byFixedGeneration(100));

		Codec<EvolutionParam<BitGene, Double>, DoubleGene> evolutionParamCodec =
			EvolutionParamCodec.general(
				IntRange.of(1, 5),
				IntRange.of(50, 500),
				IntRange.of(10, 1000),
				50,
				70
			);

		final EvolutionParam<BitGene, Double> params = optimizer
			.optimize(evolutionParamCodec, bySteadyFitness(250));

		System.out.println();
		System.out.println("Best parameters:");
		System.out.println(params);
	}

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

// This class represents a knapsack item, with a specific
// "size" and "value".
final class Item_1 {
	public final double size;
	public final double value;

	Item_1(final double size, final double value) {
		this.size = size;
		this.value = value;
	}

	// Create a new random knapsack item.
	static Item_1 random() {
		final Random r = RandomRegistry.getRandom();
		return new Item_1(r.nextDouble()*100, r.nextDouble()*100);
	}

	// Create a new collector for summing up the knapsack items.
	static Collector<Item_1, ?, Item_1> toSum() {
		return Collector.of(
			() -> new double[2],
			(a, b) -> {a[0] += b.size; a[1] += b.value;},
			(a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
			r -> new Item_1(r[0], r[1])
		);
	}
}

// The knapsack fitness function class, which is parametrized with
// the available items and the size of the knapsack.
final class FF_1
	implements Function<Genotype<BitGene>, Double>
{
	private final Item_1[] items;
	private final double size;

	public FF_1(final Item_1[] items, final double size) {
		this.items = items;
		this.size = size;
	}

	@Override
	public Double apply(final Genotype<BitGene> gt) {
		final Item_1 sum = ((BitChromosome)gt.getChromosome()).ones()
			.mapToObj(i -> items[i])
			.collect(Item_1.toSum());

		return sum.size <= this.size ? sum.value : 0;
	}
}
