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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamOptimizer<
	T,
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final Function<T, C> _fitness;
	private final Codec<T, G> _codec;
	private final Supplier<Predicate<? super EvolutionResult<?, C>>> _limit;

	/**
	 *
	 * @param fitness the {@code fitness} function for which we try to find
	 *        <i>optimal</i> evolution {@link Engine} parameters.
	 * @param codec the evolution codec for the given <i>target</i> function.
	 * @param limit the limit of the testing evolution {@code Engine}.
	 */
	public EvolutionParamOptimizer(
		final Function<T, C> fitness,
		final Codec<T, G> codec,
		final Supplier<Predicate<? super EvolutionResult<?, C>>> limit
	) {
		_fitness = requireNonNull(fitness);
		_codec = requireNonNull(codec);
		_limit = requireNonNull(limit);
	}

	/**
	 *
	 * @param codec
	 * @param limit
	 * @return
	 */
	public EvolutionParam<G, C> optimize(
		final Codec<EvolutionParam<G, C>, DoubleGene> codec,
		final Predicate<? super EvolutionResult<?, C>> limit
	) {
		final Engine<DoubleGene, C> engine = Engine
			.builder(codec.decoder().andThen(this::fitness), codec.encoding())
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
			.peek(r -> System.out.print(codec.decoder().apply(r.getBestPhenotype().getGenotype())))
			.peek(r -> System.out.println("FITNESS: " + r.getBestPhenotype().getFitness() + "\n"))
			.collect(toBestGenotype());

		return codec.decoder().apply(gt);
	}

	private C fitness(final EvolutionParam<G, C> params) {
		// The fitness function used for optimizing the Engine.
		final Function<Genotype<G>, C> ff = _fitness.compose(_codec.decoder());

		final Engine<G, C> engine = Engine.builder(ff, _codec.encoding())
			.evolutionParam(params)
			.build();

		final Genotype<G> gt = engine.stream()
			.limit(_limit.get())
			.collect(toBestGenotype());

		return ff.apply(gt);
	}









	public static void main(final String[] args) {

	}

}
