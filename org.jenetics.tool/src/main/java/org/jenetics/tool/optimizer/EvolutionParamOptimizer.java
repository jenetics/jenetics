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
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.EvolutionResult.toBestGenotype;
import static org.jenetics.engine.limit.byExecutionTime;
import static org.jenetics.engine.limit.byFixedGeneration;
import static org.jenetics.tool.optimizer.AltererCodec.ofMultiPointCrossover;
import static org.jenetics.tool.optimizer.AltererCodec.ofSwapMutator;
import static org.jenetics.tool.optimizer.SelectorCodec.ofBoltzmannSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofExponentialRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofLinearRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofTournamentSelector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.BiConsumer;
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
import org.jenetics.RouletteWheelSelector;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStream;
import org.jenetics.engine.Problem;
import org.jenetics.tool.problem.Knapsack;
import org.jenetics.tool.trial.Params;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.IO;
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
	private final Supplier<Predicate<? super EvolutionResult<?, EvolutionParamFitnessComparator<C>>>> _limit;
	private final int _sampleCount;

	/**
	 * Create a new evolution parameter optimizer.
	 *
	 * @param codec the {@code Codec} used for encoding the
	 *        {@code EvolutionParam} class
	 * @param limit the evolution stream limit which is used for terminating
	 *        the optimization of the {@code EvolutionParam} object. <b><i>Note
	 *        that this is not the terminator used for limiting the optimization
	 *        of the custom fitness function itself.</i></b>
	 * @param sampleCount the number of fitness samples used for calculating the
	 *        fitness of the evolution parameter
	 * @throws NullPointerException if the {@code codec} or {@code limit} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code sampleCount} is
	 *         smaller than 1
	 */
	public EvolutionParamOptimizer(
		final Codec<EvolutionParam<G, C>, DoubleGene> codec,
		final Supplier<Predicate<? super EvolutionResult<?, EvolutionParamFitnessComparator<C>>>> limit,
		final int sampleCount
	) {
		_codec = requireNonNull(codec);
		_limit = requireNonNull(limit);
		_sampleCount = require.positive(sampleCount);
	}

	/**
	 * Create a new evolution parameter optimizer.
	 *
	 * @param codec the {@code Codec} used for encoding the
	 *        {@code EvolutionParam} class
	 * @param limit the evolution stream limit which is used for terminating
	 *        the optimization of the {@code EvolutionParam} object. <b><i>Note
	 *        that this is not the terminator used for limiting the optimization
	 *        of the custom fitness function itself.</i></b>
	 * @throws NullPointerException if the {@code codec} or {@code limit} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code sampleCount} is
	 *         smaller than 1
	 */
	public EvolutionParamOptimizer(
		final Codec<EvolutionParam<G, C>, DoubleGene> codec,
		final Supplier<Predicate<? super EvolutionResult<?, EvolutionParamFitnessComparator<C>>>> limit
	) {
		this(codec, limit, 10);
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
	 * @param callback Callback of intermediate results
	 * @param <T> the fitness parameter type
	 * @return the found <i>optimal</i> evolution parameters for your given
	 *         fitness function.
	 */
	public <T> EvolutionParam<G, C> optimize(
		final Problem<T, G, C> problem,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit,
		final BiConsumer<EvolutionResult<?, ?>, EvolutionParam<G, C>> callback
	) {
		final Function<EvolutionParam<G, C>, EvolutionParamFitnessComparator<C>>
		evolutionParamFitness = p -> evolutionParamFitness(
			p, problem.fitness(), problem.codec(), optimize, limit
		);

		final Engine<DoubleGene, EvolutionParamFitnessComparator<C>> engine =
			engine(evolutionParamFitness, optimize);


		final Genotype<DoubleGene> gt = engine.stream()
			.limit(_limit.get())
			.peek(result -> callback.accept(
					result,
					_codec.decoder().apply(result.getBestPhenotype().getGenotype())))
			.peek(result -> {
				/*
				try {
					IO.jaxb.write(ISeq.of(result.getPopulation()).map(pt -> pt.getGenotype()).asList(), System.out);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				*/
			})
			.collect(toBestGenotype());

		return _codec.decoder().apply(gt);
	}

	public <T> Stream<OptimizerResult<C>> stream(
		final Problem<T, G, C> problem,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {



		final Function<EvolutionParam<G, C>, EvolutionParamFitnessComparator<C>> evolutionParamFitness =
			p -> evolutionParamFitness(
					p, problem.fitness(), problem.codec(), optimize, limit
				);

		final Engine<DoubleGene, EvolutionParamFitnessComparator<C>> engine =
			engine(evolutionParamFitness, optimize);

		EvolutionStream<DoubleGene, EvolutionParamFitnessComparator<C>> stream = engine.stream();

		return stream.map(this::toOptimizerResult);
	}

	private OptimizerResult<C> toOptimizerResult(
		final EvolutionResult<DoubleGene, EvolutionParamFitnessComparator<C>> result
	) {
		return OptimizerResult.of(
			null, //result,
			null //_codec.decoder().apply(result.getBestPhenotype().getGenotype())
		);
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
	public <T> EvolutionParam<G, C> optimize(
		final Problem<T, G, C> problem,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		return optimize(problem, optimize, limit, this::println);
	}

	private void println(
		final EvolutionResult<?, ?> result,
		final EvolutionParam<G, C> param
	) {
		final String output =
		"Generation:         " + result.getTotalGenerations() + "\n" +
		param + "\n" +
		"Fitness:            " + result.getBestFitness() + "\n";

		System.out.println(output);
	}

	/**
	 * Create a new evolution {@code Engine} for optimizing the evolution
	 * parameters.
	 *
	 * @param fitness the fitness function of given evolution parameter.
	 * @param optimize the optimization strategy
	 * @return a new optimization evolution engine
	 */
	private Engine<DoubleGene, EvolutionParamFitnessComparator<C>> engine(
		final Function<EvolutionParam<G, C>, EvolutionParamFitnessComparator<C>> fitness,
		final Optimize optimize
	) {
		final Function<Genotype<DoubleGene>, EvolutionParamFitnessComparator<C>> ff =
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
	 * @param params the evolution parameters to test
	 * @param fitness the fitness function for which we want to optimize the
	 *        evolution parameters
	 * @param codec the fitness function codec
	 * @param optimize the optimization strategy
	 * @param limit the evolution stream limit used for terminating the
	 *        <i>test</i> engine
	 * @param <T> the parameter type of the fitness function
	 * @return the fitness value for the given evolution parameters
	 */
	private <T> EvolutionParamFitnessComparator<C> evolutionParamFitness(
		final EvolutionParam<G, C> params,
		final Function<T, C> fitness,
		final Codec<T, G> codec,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		final Engine<G, C> engine = Engine.builder(fitness, codec)
			.evolutionParam(params)
			.optimize(optimize)
			.build();

		final Stream<C> results = Stream.generate(() -> {
					final Genotype<G> gt = engine.stream()
						.limit(limit.get())
						.collect(toBestGenotype());
					return fitness.compose(codec.decoder()).apply(gt);
				})
			.limit(_sampleCount);

		final ISeq<EvolutionParamFitnessComparator<C>> measures = results
			.map(c -> new EvolutionParamFitnessComparator<>(c, params, optimize))
			.sorted(optimize.ascending())
			.collect(ISeq.toISeq());

		// Return the median value.
		return measures.get(measures.length()/2);
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
		final Path dir = command.arg("dir")
			.map(d -> Paths.get(d))
			.orElse(Paths.get(".").toAbsolutePath());

		final Path progressPath = Paths.get(dir.toString(), "progress.properties");
		final Properties progress = new Properties();
		if (Files.exists(progressPath)) {
			try (FileInputStream in = new FileInputStream(progressPath.toFile())) {
				progress.load(in);
			}
		} else {
			//progress.setProperty("", "");
		}

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

		final EvolutionParamOptimizer<BitGene, Double>
		optimizer = new EvolutionParamOptimizer<>(
			codec,
			() -> byFixedGeneration(100),
			5
		);

		final EvolutionParam<BitGene, Double> params = optimizer.optimize(
			problem,
			Optimize.MAXIMUM,
			() -> byExecutionTime(ofMillis(150)),
			(r, p) -> {
				try {
					IO.jaxb.write(p, System.out);
				} catch(IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		);

		System.out.println();
		System.out.println("Best parameters:");
		System.out.println(params);
	}

}
