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

import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.EvolutionResult.toBestGenotype;
import static org.jenetics.engine.limit.byFixedGeneration;
import static org.jenetics.engine.limit.bySteadyFitness;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumericGene;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EngineOptimizer<
	T,
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	private final Function<T, C> _fitness;
	private final Codec<T, G> _codec;
	private final Supplier<Predicate<? super EvolutionResult<?, C>>> _limit;

	public EngineOptimizer(
		final Function<T, C> fitness,
		final Codec<T, G> codec,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		_fitness = requireNonNull(fitness);
		_codec = requireNonNull(codec);
		_limit = requireNonNull(limit);
	}

	public Parameters<G, C> optimize(
		final Codec<Parameters<G, C>, DoubleGene> codec,
		final Predicate<? super EvolutionResult<?, C>> limit
	) {
		final Engine<DoubleGene, C> engine = Engine
			.builder(codec.decoder().andThen(this::opt), codec.encoding())
			.alterers(
				new MeanAlterer<>(0.25),
				new GaussianMutator<>(0.25),
				new Mutator<>(0.5),
				new SinglePointCrossover<>())
			.offspringSelector(new TournamentSelector<>(2))
			.survivorsSelector(new TournamentSelector<>(5))
			.populationSize(50)
			.maximalPhenotypeAge(5)
			.build();

		final Genotype<DoubleGene> gt = engine.stream()
			.limit(limit)
			.peek(r -> System.out.println("Generation: " + r.getTotalGenerations()))
			.peek(r -> print(codec.decoder().apply(r.getBestPhenotype().getGenotype())))
			.peek(r -> System.out.println("FITNESS: " + r.getBestPhenotype().getFitness() + "\n"))
			.collect(toBestGenotype());

		return codec.decoder().apply(gt);
	}

	private void print(final Parameters<G, C> params) {
		System.out.println(params);
	}

	// The Engine parameter optimizer fitness function.
	private C opt(final Parameters<G, C> params) {
		// The fitness function used for optimizing the Engine.
		final Function<Genotype<G>, C> ff = _fitness.compose(_codec.decoder());

		final Engine<G, C> engine = Engine.builder(ff, _codec.encoding())
			.alterers(params.getAlterers())
			.offspringSelector(params.getOffspringSelector())
			.survivorsSelector(params.getSurvivorsSelector())
			.offspringFraction(params.getOffspringFraction())
			.populationSize(params.getPopulationSize())
			.maximalPhenotypeAge(params.getMaximalPhenotypeAge())
			.build();

		final Genotype<G> gt = engine.stream()
			.limit(_limit.get())
			.collect(toBestGenotype());

		return ff.apply(gt);
	}

	public static void main(final String[] args) {
		//final Function<Double, Double> fitness = x -> cos(0.5 + sin(x))*cos(x);
		//final Codec<DoubleGene, Double> codec = Codec.ofDouble(0.0, 2*Math.PI);

		final int nitems = 200;
		final double kssize = nitems*100.0/3.0;

		final FF fitness =
			RandomRegistry.with(new LCG64ShiftRandom(1234), r ->
				new FF(
					Stream.generate(Item::random)
						.limit(nitems)
						.toArray(Item[]::new),
					kssize
				)
			);

		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
		final Codec<Genotype<BitGene>, BitGene> codec = Codec.of(
			Genotype.of(BitChromosome.of(nitems, 0.5)),
			Function.<Genotype<BitGene>>identity()
		);

		final EngineOptimizer<Genotype<BitGene>, BitGene, Double> optimizer =
			new EngineOptimizer<>(fitness, codec, () -> byFixedGeneration(100));

		final Parameters<BitGene, Double> params = optimizer
			.optimize(numberCodec(), bySteadyFitness(250));

		System.out.println();
		System.out.println("Best parameters:");
		System.out.println(params);
	}


	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Parameters<G, C>, DoubleGene> codec() {
		return new ParametersCodec<>(
			Alterers.<G, C>general(),
			Selectors.<G, C>generic(),
			Selectors.<G, C>generic(),
			IntRange.of(10, 5000),
			IntRange.of(5, 100)
		);
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<Parameters<G, C>, DoubleGene> numericCodec() {
		return new ParametersCodec<>(
			Alterers.<G, C>numeric(),
			Selectors.<G, C>generic(),
			Selectors.<G, C>generic(),
			IntRange.of(10, 5000),
			IntRange.of(5, 100)
		);
	}

	public static <G extends Gene<?, G>, C extends Number & Comparable<? super C>>
	Codec<Parameters<G, C>, DoubleGene> numberCodec() {
		return new ParametersCodec<>(
			Alterers.<G, C>general(),
			Selectors.<G, C>number(),
			Selectors.<G, C>number(),
			IntRange.of(100, 120),
			IntRange.of(5, 100)
		);
	}

	public static <G extends NumericGene<?, G>, C extends Number & Comparable<? super C>>
	Codec<Parameters<G, C>, DoubleGene> numericNumberCodec() {
		return new ParametersCodec<>(
			Alterers.<G, C>numeric(),
			Selectors.<G, C>number(),
			Selectors.<G, C>number(),
			IntRange.of(10, 5000),
			IntRange.of(5, 100)
		);
	}


}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

// This class represents a knapsack item, with a specific
// "size" and "value".
final class Item {
	public final double size;
	public final double value;

	Item(final double size, final double value) {
		this.size = size;
		this.value = value;
	}

	// Create a new random knapsack item.
	static Item random() {
		final Random r = RandomRegistry.getRandom();
		return new Item(r.nextDouble()*100, r.nextDouble()*100);
	}

	// Create a new collector for summing up the knapsack items.
	static Collector<Item, ?, Item> toSum() {
		return Collector.of(
			() -> new double[2],
			(a, b) -> {a[0] += b.size; a[1] += b.value;},
			(a, b) -> {a[0] += b[0]; a[1] += b[1]; return a;},
			r -> new Item(r[0], r[1])
		);
	}
}

// The knapsack fitness function class, which is parametrized with
// the available items and the size of the knapsack.
final class FF
	implements Function<Genotype<BitGene>, Double>
{
	private final Item[] items;
	private final double size;

	public FF(final Item[] items, final double size) {
		this.items = items;
		this.size = size;
	}

	@Override
	public Double apply(final Genotype<BitGene> gt) {
		final Item sum = ((BitChromosome)gt.getChromosome()).ones()
			.mapToObj(i -> items[i])
			.collect(Item.toSum());

		return sum.size <= this.size ? sum.value : 0;
	}
}

