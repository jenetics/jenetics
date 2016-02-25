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
package org.jenetics.tool.optimizer;

import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.EvolutionResult.toBestEvolutionResult;
import static org.jenetics.engine.limit.byExecutionTime;
import static org.jenetics.tool.optimizer.AltererCodec.ofMultiPointCrossover;
import static org.jenetics.tool.optimizer.AltererCodec.ofSwapMutator;
import static org.jenetics.tool.optimizer.SelectorCodec.ofBoltzmannSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofExponentialRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofLinearRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofTournamentSelector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.internal.util.Args;
import org.jenetics.internal.util.require;

import org.jenetics.BitGene;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.Problem;
import org.jenetics.tool.problem.Knapsack;
import org.jenetics.tool.trial.Params;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.LongRange;

/**
 * Optimizer for finding <i>optimal</i> evolution engine parameters.
 *
 * @param <G> the gene type of the problem encoding
 * @param <C> the fitness function return type of the problem encoding
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamOptimizer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final Codec<EvolutionParam<G, C>, DoubleGene> _codec;
	private final int _sampleCount;

	/**
	 * Create a new evolution parameter optimizer.
	 *
	 * @param codec the {@code Codec} used for encoding the
	 *        {@code EvolutionParam} class
	 * @param sampleCount the number of fitness samples used for calculating the
	 *        fitness of the evolution parameter
	 * @throws NullPointerException if the {@code codec} or {@code limit} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code sampleCount} is
	 *         smaller than 1
	 */
	public EvolutionParamOptimizer(
		final Codec<EvolutionParam<G, C>, DoubleGene> codec,
		final int sampleCount
	) {
		_codec = requireNonNull(codec);
		_sampleCount = require.positive(sampleCount);
	}

	/**
	 * Create a new evolution parameter optimizer.
	 *
	 * @param codec the {@code Codec} used for encoding the
	 *        {@code EvolutionParam} class
	 * @throws NullPointerException if the {@code codec} or {@code limit} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code sampleCount} is
	 *         smaller than 1
	 */
	public EvolutionParamOptimizer(
		final Codec<EvolutionParam<G, C>, DoubleGene> codec
	) {
		this(codec, 10);
	}

	/**
	 * Return the evolution parameters which are considered to be "optimal" for
	 * your given problem. The given parameters are the same as you will use
	 * for a <i>normal</i> {@code Engine} instantiation.
	 *
	 * @param problem the problem for which the evolution parameters should be
	 *        optimized
	 * @param optimize the optimization strategy of the problem
	 * @param limit the limit of the testing evolution {@code Engine}
	 * @param <T> the fitness parameter type
	 * @return the found <i>optimal</i> evolution parameters for your given
	 *         fitness function.
	 */
	public <T> Stream<OptimizerResult<G, C>> stream(
		final Problem<T, G, C> problem,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit,
		final Iterable<Genotype<DoubleGene>> initialGenotypes,
		final long generation
	) {
		final Function<EvolutionParam<G, C>, OptimizerFitness<G, C>>
		evolutionParamFitness = p -> evolutionParamFitness(
					p, problem.fitness(), problem.codec(), optimize, limit
				);

		final Engine<DoubleGene, OptimizerFitness<G, C>> engine =
			engine(evolutionParamFitness, optimize);

		return engine.stream(initialGenotypes, generation)
			.map((EvolutionResult<DoubleGene, OptimizerFitness<G, C>> result) -> {
				final ISeq<Genotype<DoubleGene>> genotypes = result.getPopulation()
					.stream()
					.map(Phenotype::getGenotype)
					.collect(ISeq.toISeq());

				final EvolutionParam<G, C> params = _codec.decoder()
					.apply(result.getBestPhenotype().getGenotype());

				return new OptimizerResult<G, C>(
					genotypes,
					params,
					result.getBestFitness().getFitness(),
					result.getTotalGenerations()
				);
			});
	}

	public <T> Stream<OptimizerResult<G, C>> stream(
		final Problem<T, G, C> problem,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		return stream(problem, optimize, limit, Collections.emptyList(), 1);
	}


	/**
	 * Create a new evolution {@code Engine} for optimizing the evolution
	 * parameters.
	 *
	 * @param fitness the fitness function of given evolution parameter.
	 * @param optimize the optimization strategy
	 * @return a new optimization evolution engine
	 */
	private Engine<DoubleGene, OptimizerFitness<G, C>> engine(
		final Function<EvolutionParam<G, C>, OptimizerFitness<G, C>> fitness,
		final Optimize optimize
	) {
		final Function<Genotype<DoubleGene>, OptimizerFitness<G, C>> ff =
			_codec.decoder().andThen(fitness);

		return Engine.builder(ff, _codec.encoding())
			.alterers(
				new MeanAlterer<>(0.25),
				new GaussianMutator<>(0.25),
				new Mutator<>(0.05))
			.survivorsSelector(new TruncationSelector<>())
			.offspringSelector(new TournamentSelector<>(3))
			.populationSize(100)
			.maximalPhenotypeAge(35)
			.optimize(optimize)
			.build();
	}

	/**
	 * Calculate the fitness of the given evolution parameters for the given
	 * custom fitness function.
	 *
	 * @param param the evolution parameters to test
	 * @param fitness the fitness function for which we want to optimize the
	 *        evolution parameters
	 * @param codec the fitness function codec
	 * @param optimize the optimization strategy
	 * @param limit the evolution stream limit used for terminating the
	 *        <i>test</i> engine
	 * @param <T> the parameter type of the fitness function
	 * @return the fitness value for the given evolution parameters
	 */
	private <T> OptimizerFitness<G, C> evolutionParamFitness(
		final EvolutionParam<G, C> param,
		final Function<T, C> fitness,
		final Codec<T, G> codec,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		// The evolution engine of the target problem.
		final Engine<G, C> engine = Engine.builder(fitness, codec)
			.evolutionParam(param)
			.optimize(optimize)
			.build();

		final ISeq<OptimizerFitness<G, C>> results = Stream.generate(() ->
				engine.stream()
					.limit(limit.get())
					.collect(toBestEvolutionResult()))
			.limit(_sampleCount)
			.map(result -> OptimizerFitness.of(result, param))
			.sorted(optimize.ascending())
			.collect(ISeq.toISeq());

		// Return the median value.
		return results.get(results.length()/2);
	}


	/* *************************************************************************
	 * Test main method
	 **************************************************************************/

	private static final double GEN_BASE = pow(10, log10(100)/20.0);
	private static final Params<Long> PARAMS = Params.of(
		"Sample count",
		IntStream.rangeClosed(1, 30)
			.mapToLong(i -> max((long)pow(GEN_BASE, i), i))
			.mapToObj(Long::new)
			.collect(ISeq.toISeq())
	);

	public static void main(final String[] args) throws IOException {
		final Args command = Args.of(args);
		final Path dir = command.arg("d")
			.map(d -> Paths.get(d))
			.orElse(Paths.get("C:\\Users\\franz\\results").toAbsolutePath());

		final ISeq<Genotype<DoubleGene>> startPopulation;
		final long generation;
		if (Files.exists(dir)) {
			final Optional<OptimizerResult<BitGene, Double>> result =
			Stream.of(dir.toFile().listFiles(f -> f.toString().endsWith("xml")))
				.sorted(Comparator.reverseOrder())
				.findFirst()
				.map(OptimizerResult::read);

			startPopulation = result
				.map(OptimizerResult::getGenotypes)
				.orElse(ISeq.empty());
			generation = result
				.map(OptimizerResult::getGeneration)
				.orElse(0L);
		} else {
			startPopulation = ISeq.empty();
			generation = 0L;
		}

		System.out.println("Continue at generation " + (generation + 1) + ".");

		// The problem fow which to optimize the EvolutionParams.
		final Knapsack problem = Knapsack.of(250, new LCG64ShiftRandom(10101));

		final IntRange populationSize = IntRange.of(10, 1_000);
		final DoubleRange offspringFraction = DoubleRange.of(0, 1);
		final LongRange maximalPhenotypeAge = LongRange.of(5, 10_000);

		final EvolutionParamCodec<BitGene, Double> codec =
			EvolutionParamCodec.of(
				SelectorCodec
					.of(new RouletteWheelSelector<BitGene, Double>())
					.and(new TruncationSelector<>())
					.and(new StochasticUniversalSelector<>())
					.and(ofBoltzmannSelector(DoubleRange.of(0, 3)))
					.and(ofExponentialRankSelector(DoubleRange.of(0, 1)))
					.and(ofLinearRankSelector(DoubleRange.of(0, 3)))
					.and(ofTournamentSelector(IntRange.of(2, 10))),
				AltererCodec.<BitGene, Double>ofMutator()
					.and(ofMultiPointCrossover(IntRange.of(2, 20)))
					.and(ofSwapMutator()),
				populationSize,
				offspringFraction,
				maximalPhenotypeAge
			);

		final EvolutionParamOptimizer<BitGene, Double> optimizer =
			new EvolutionParamOptimizer<>(codec, 25);

		Stream<OptimizerResult<BitGene, Double>> stream = optimizer.stream(
			problem,
			Optimize.MAXIMUM,
			() -> byExecutionTime(ofMillis(150)),
			startPopulation,
			generation + 1
		);

		stream.limit(1000).forEach(r -> {
			System.out.println(format(
				"Generation=%d, Fitness=%f",
				r.getGeneration(), r.getFitness()
			));

			r.write(new File("C:\\Users\\franz\\results"));
		});

	}

}
