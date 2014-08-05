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
package org.jenetics.internal.engine;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-05 $</em>
 */
public class GA<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	// Selectors
	private Selector<G, C> _survivorSelector = new TournamentSelector<>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);
	private Alterer<G, C> _alterer = null;

	private Context<G, C> _context = null;

	private final SelectStage<G, C> _selection = new SelectStage<>(_context);
	private final AlterStage<G, C> _altering = new AlterStage<>(_context);

	private Function<Population<G, C>, Population<G, C>> _selector =
		p -> _survivorSelector.select(p, _context.getSurvivorCount(), _context.getOptimize());

	public GA(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<? super Genotype<G>, ? extends C> fitnessFunction,
		final Function<? super C, ? extends C> fitnessScaler,
		final Optimize optimization,
		final Executor executor
	) {
//		_genotypeFactory = requireNonNull(genotypeFactory, "GenotypeFactory");
//		_fitnessFunction = requireNonNull(fitnessFunction, "FitnessFunction");
//		_fitnessScaler = requireNonNull(fitnessScaler, "FitnessScaler");
//		_optimization = requireNonNull(optimization, "Optimization");
//		_executor = requireNonNull(executor, "Executor");
//
//		_phenotypeFactory = () -> Phenotype.of(
//			_genotypeFactory.newInstance(),
//			_fitnessFunction,
//			_fitnessScaler,
//			_generation
//		);
	}

	public State<G, C> evolve(final State<G, C> state) {
		final CompletionStage<TimedResult<Population<G, C>>> offspring = async(() ->
			_context.getOffspringSelector().select(
				state.getPopulation(),
				_context.getOffspringCount(),
				_context.getOptimize()
			)
		);
		final CompletionStage<TimedResult<Population<G, C>>> survivor = async(() ->
			_context.getSurvivorSelector().select(
				state.getPopulation(),
				_context.getSurvivorCount(),
				_context.getOptimize()
			)
		);

		final CompletionStage<TimedResult<Integer>> altered = then(offspring, population ->
			_alterer.alter(population.get(), state.getGeneration())
		);

		final CompletionStage<Population<G, C>> population = survivor.thenCombineAsync(
			altered,
			(s, a) -> {
				final Population<G, C> pop = s.get();
				pop.addAll(offspring.toCompletableFuture().join().get());
				return pop;
			},
			_context.getExecutor()
		);

		/*
		final SelectStage.Result<G, C> select = _selection.select(null);

		final CompletionStage<Integer> alter = select.getOffspring().thenApplyAsync(o ->
			_context.getAlterer().alter(o, 10), _context.getExecutor()
		);
		*/


		return null;
	}

	private <T> CompletionStage<TimedResult<T>> async(final Supplier<T> supplier) {
		return supplyAsync(TimedResult.of(supplier), _context.getExecutor());
	}

	private <U, T> CompletionStage<TimedResult<T>> then(
		final CompletionStage<U> result,
		final Function<U, T> fn
	) {
		return result.thenApplyAsync(TimedResult.of(fn), _context.getExecutor());
	}

	/**
	 * Represents a state of the GA.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 */
	public static final class State<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		private final Population<G, C> _population;
		private final int _generation;

		public State(final Population<G, C> population, final int generation) {
			_population = requireNonNull(population);
			_generation = generation;
		}

		public Population<G, C> getPopulation() {
			return _population;
		}

		public int getGeneration() {
			return _generation;
		}
	}

	/**
	 * Represents a configuration of the GA.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 */
	public static final class Config<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{

	}

}
