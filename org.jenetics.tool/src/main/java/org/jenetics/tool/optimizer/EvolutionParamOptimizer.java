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

import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.EvolutionResult.toBestGenotype;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.Problem;
import org.jenetics.util.ISeq;

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
	private final Supplier<Predicate<? super EvolutionResult<?, Measure<C>>>> _limit;

	/**
	 * Create a new evolution parameter optimizer.
	 *
	 * @param codec the {@code Codec} used for encoding the
	 *        {@code EvolutionParam} class
	 * @param limit the evolution stream limit which is used for terminating
	 *        the optimization of the {@code EvolutionParam} object. <b><i>Note
	 *        that this is not the terminator used for limiting the optimization
	 *        of the custom fitness function itself.</i></b>
	 */
	public EvolutionParamOptimizer(
		final Codec<EvolutionParam<G, C>, DoubleGene> codec,
		final Supplier<Predicate<? super EvolutionResult<?, Measure<C>>>> limit
	) {
		_codec = requireNonNull(codec);
		_limit = requireNonNull(limit);
	}

	/**
	 * Return the evolution parameters which are considered to be "optimal" for
	 * your given problem. The given parameters are the same as you will use
	 * for a <i>normal</i> {@code Engine} instantiation.
	 *
	 * @param fitness the {@code fitness} function of your problem
	 * @param codec the evolution codec for the given <i>target</i> function.
	 * @param optimize the optimization strategy
	 * @param limit the limit of the testing evolution {@code Engine}.
	 * @param <T> the fitness parameter type
	 * @return the found <i>optimal</i> evolution parameters for your given
	 *         fitness function.
	 */
	public <T> EvolutionParam<G, C> optimize(
		final Function<T, C> fitness,
		final Codec<T, G> codec,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		final Function<EvolutionParam<G, C>, Measure<C>> evolutionParamFitness = p ->
			evolutionParamFitness(p, fitness, codec, optimize, limit);

		final Engine<DoubleGene, Measure<C>> engine =
			engine(evolutionParamFitness, optimize);

		final Genotype<DoubleGene> gt = engine.stream()
			.limit(_limit.get())
			.peek(this::println)
			.collect(toBestGenotype());

		return _codec.decoder().apply(gt);
	}

	/**
	 * Return the evolution parameters which are considered to be "optimal" for
	 * your given problem. The given parameters are the same as you will use
	 * for a <i>normal</i> {@code Engine} instantiation.
	 *
	 * @param problem the problem for which the evolution parameters should be
	 *        optimized
	 * @param optimize the optimization strategy of the problem
	 * @param limit the limit of the testing evolution {@code Engine}.
	 * @param <T> the fitness parameter type
	 * @return the found <i>optimal</i> evolution parameters for your given
	 *         fitness function.
	 */
	public <T> EvolutionParam<G, C> optimize(
		final Problem<T, G, C> problem,
		final Optimize optimize,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		return optimize(
			problem.fitness(),
			problem.codec(),
			optimize,
			limit
		);
	}

	private void println(final EvolutionResult<DoubleGene, Measure<C>> result) {
		final EvolutionParam<G, C> param = _codec.decoder()
			.apply(result.getBestPhenotype().getGenotype());

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
	private Engine<DoubleGene, Measure<C>> engine(
		final Function<EvolutionParam<G, C>, Measure<C>> fitness,
		final Optimize optimize
	) {
		final Function<Genotype<DoubleGene>, Measure<C>> ff =
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
	private <T> Measure<C> evolutionParamFitness(
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

		final Stream<C> results = IntStream.range(0, 20).mapToObj(i -> {
			final Genotype<G> gt = engine.stream()
				.limit(limit.get())
				.collect(toBestGenotype());

			return fitness.compose(codec.decoder()).apply(gt);
		});

		final ISeq<Measure<C>> measures = results
			.map(c -> new Measure<>(c, params, optimize))
			.sorted(optimize.ascending())
			.collect(ISeq.toISeq());

		// Return the median value.
		return measures.get(measures.length()/2);
	}

}
