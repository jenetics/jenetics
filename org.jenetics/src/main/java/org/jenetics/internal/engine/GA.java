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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jenetics.internal.engine.AlterStage.Result;
import org.jenetics.internal.engine.CombineStage.CombineResult;
import org.jenetics.internal.util.Timer;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-04 $</em>
 */
public class GA<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	// Selectors
	private Selector<G, C> _survivorSelector = new TournamentSelector<>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);

	private Context<G, C> _context = null;

	private final SelectStage<G, C> _selection = new SelectStage<>(_context);
	private final AlterStage<G, C> _altering = new AlterStage<>(_context);

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
			selectOffspring(state.getPopulation())
		);

		final SelectStage.Result<G, C> select = _selection.select(null);

		final CompletionStage<Integer> alter = select.getOffspring().thenApplyAsync(o ->
			_context.getAlterer().alter(o, 10), _context.getExecutor()
		);


		return null;
	}

	private Population<G, C> selectOffspring(final Population<G, C> population) {
		return _context.getOffspringSelector().select(
			population, _context.getOffspringCount(), _context.getOptimize()
		);
	}

	private Population<G, C> selectSurvivor(final Population<G, C> population) {
		return _context.getSurvivorSelector().select(
			population, _context.getSurvivorCount(), _context.getOptimize()
		);
	}

	private <T> CompletionStage<TimedResult<T>> async(final Supplier<T> supplier) {
		final Supplier<TimedResult<T>> wrapped = () -> TimedResult.of(supplier);
		return CompletableFuture.supplyAsync(wrapped, _context.getExecutor());
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
