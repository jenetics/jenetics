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

import java.util.concurrent.Executor;
import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-01 $</em>
 */
public class GA<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private Context<G, C> _context = null;

	private final SelectStage<G, C> _selection = new SelectStage<>(_context);

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

	public void evolve() {
		final SelectStage.Result<G, C> select = _selection.select(null);
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
