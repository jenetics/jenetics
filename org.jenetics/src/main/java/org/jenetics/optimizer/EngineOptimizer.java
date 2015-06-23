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

import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.EvolutionResult.toBestGenotype;
import static org.jenetics.engine.limit.byExecutionTime;
import static org.jenetics.engine.limit.byFixedGeneration;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.NumericGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;

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
	private final Codec<G, T> _codec;
	private final Predicate<? super EvolutionResult<?, C>> _limit;

	public EngineOptimizer(
		final Function<T, C> fitness,
		final Codec<G, T> codec,
		final Predicate<? super EvolutionResult<?, C>> limit
	) {
		_fitness = requireNonNull(fitness);
		_codec = requireNonNull(codec);
		_limit = requireNonNull(limit);
	}

	public Parameters<G, C> optimize(
		final Codec<DoubleGene, Parameters<G, C>> codec,
		final Predicate<? super EvolutionResult<?, C>> limit
	) {
		final Function<Parameters<G, C>, C> pff = this::opt;

		final Engine<DoubleGene, C> engine = Engine
			.builder(pff.compose(codec.decoder()), codec.encoding())
			.build();

		final Genotype<DoubleGene> gt = engine.stream()
			.limit(limit)
			.collect(toBestGenotype());

		return codec.decoder().apply(gt);
	}

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
			.limit(_limit)
			.collect(toBestGenotype());

		return ff.apply(gt);
	}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


	static <G extends Gene<?, G>, C extends Comparable<? super C>> C fitness(
		final int generations,
		final Parameters<G, C> params,
		final Codec<G, C> codec,
		final Function<Genotype<G>, C> ff
	) {
		final Engine<G, C> engine = Engine.builder(ff, codec.encoding())
			.alterers(params.getAlterers())
			.offspringSelector(params.getOffspringSelector())
			.survivorsSelector(params.getSurvivorsSelector())
			.offspringFraction(params.getOffspringFraction())
			.populationSize(params.getPopulationSize())
			.maximalPhenotypeAge(params.getMaximalPhenotypeAge())
			.build();

		final Genotype<G> gt = engine.stream()
			.limit(generations)
			.collect(toBestGenotype());

		return codec.decoder().apply(gt);
	}

	static double problemFitness(final Genotype<DoubleGene> gt) {
		return Math.sin(gt.getGene().doubleValue());
	}

	public static void main(final String[] args) {
		final Function<Double, Double> fitness = Math::sin;
		final Codec<DoubleGene, Double> codec = Codec.ofDouble(0.0, 2*Math.PI);

		final EngineOptimizer<Double, DoubleGene, Double> optimizer =
			new EngineOptimizer<>(fitness, codec, byExecutionTime(ofMillis(500)));

		final Parameters<DoubleGene, Double> params = optimizer
			.optimize(numericNumberCodec(), byFixedGeneration(10));

		System.out.println(params);

		/*
		final Codec<DoubleGene, Double> problemCodec = Codec.ofDouble(0.0, 2*Math.PI);

		final Codec<DoubleGene, Parameters<DoubleGene, Double>> parametersCodec =
			new ParametersCodec<>(
				Alterers.numericMean(),
				ISeq.of(
					new BoltzmannSelectorProxy<DoubleGene, Double>(1),
					new ExponentialRankSelectorProxy<DoubleGene, Double>(1),
					new LinearRankSelectorProxy<DoubleGene, Double>(1),
					new RouletteWheelSelectorProxy<DoubleGene, Double>(1),
					new StochasticUniversalSelectorProxy<DoubleGene, Double>(1),
					new TournamentSelectorProxy<DoubleGene, Double>(1.0, 5),
					new TruncationSelectorProxy<DoubleGene, Double>(1)
				),
				ISeq.of(
					new BoltzmannSelectorProxy<DoubleGene, Double>(1),
					new ExponentialRankSelectorProxy<DoubleGene, Double>(1),
					new LinearRankSelectorProxy<DoubleGene, Double>(1),
					new RouletteWheelSelectorProxy<DoubleGene, Double>(1),
					new StochasticUniversalSelectorProxy<DoubleGene, Double>(1),
					new TournamentSelectorProxy<DoubleGene, Double>(1.0, 5),
					new TruncationSelectorProxy<DoubleGene, Double>(1)
				),
				50, 100,
				10, 1000
			);

		final Function<Parameters<DoubleGene, Double>, Double> pff = p -> {
			return fitness(20, p, problemCodec, EngineOptimizer::problemFitness);
		};

		final Engine<DoubleGene, Double> engine = Engine
			.builder(pff.compose(parametersCodec.decoder()), parametersCodec.encoding())
			.build();

		final Genotype<DoubleGene> gt = engine.stream()
			.limit(20)
			.collect(toBestGenotype());

		final Parameters<DoubleGene, Double> params = parametersCodec.decoder().apply(gt);
		System.out.println(params);
		System.out.println();
		System.out.println();
		*/
	}


	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<DoubleGene, Parameters<G, C>> codec() {
		return new ParametersCodec<>(
			Alterers.<G, C>general(),
			Selectors.<G, C>generic(),
			Selectors.<G, C>generic(),
			50, 100,
			10, 1000
		);
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<DoubleGene, Parameters<G, C>> numericCodec() {
		return new ParametersCodec<>(
			Alterers.<G, C>numeric(),
			Selectors.<G, C>generic(),
			Selectors.<G, C>generic(),
			50, 100,
			10, 1000
		);
	}

	public static <G extends Gene<?, G>, C extends Number & Comparable<? super C>>
	Codec<DoubleGene, Parameters<G, C>> numberCodec() {
		return new ParametersCodec<>(
			Alterers.<G, C>general(),
			Selectors.<G, C>number(),
			Selectors.<G, C>number(),
			50, 100,
			10, 1000
		);
	}

	public static <G extends NumericGene<?, G>, C extends Number & Comparable<? super C>>
	Codec<DoubleGene, Parameters<G, C>> numericNumberCodec() {
		return new ParametersCodec<>(
			Alterers.<G, C>numeric(),
			Selectors.<G, C>number(),
			Selectors.<G, C>number(),
			50, 100,
			10, 1000
		);
	}


}
