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
package org.jenetics.engine;

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.require.probability;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Selector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class EngineBuilder<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
{

	// No default values for this properties.
	private Function<? super Genotype<G>, ? extends C> _fitnessFunction;
	private Factory<Genotype<G>> _genotypeFactory;

	// This are the properties which default values.
	private Function<? super C, ? extends C> _fitnessScaler = Function.identity();
	private Selector<G, C> _survivorsSelector = new TournamentSelector<>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);
	private Alterer<G, C> _alterer = Alterer.of(
		new SinglePointCrossover<G, C>(0.2),
		new Mutator<G, C>(0.15)
	);
	private Optimize _optimize = Optimize.MAXIMUM;
	private double _offspringFraction = 0.6;
	private int _populationSize = 50;
	private int _maximalPhenotypeAge = 70;

	private Executor _executor = ForkJoinPool.commonPool();

	public EngineBuilder(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<? super Genotype<G>, ? extends C> fitnessFunction
	) {
		_genotypeFactory = requireNonNull(genotypeFactory);
		_fitnessFunction = requireNonNull(fitnessFunction);
	}

	public EngineBuilder<G, C> fitnessFunction(
		Function<? super Genotype<G>, ? extends C> function
	) {
		_fitnessFunction = requireNonNull(function);
		return this;
	}

	public EngineBuilder<G, C> fitnessScaler(
		final Function<? super C, ? extends C> scaler
	) {
		_fitnessScaler = requireNonNull(scaler);
		return this;
	}

	public EngineBuilder<G, C> genotypeFactory(
		final Factory<Genotype<G>> genotypeFactory
	) {
		_genotypeFactory = requireNonNull(genotypeFactory);
		return this;
	}

	public EngineBuilder<G, C> offspringSelector(
		final Selector<G, C> selector
	) {
		_offspringSelector = requireNonNull(selector);
		return this;
	}

	public EngineBuilder<G, C> survivorsSelector(
		final Selector<G, C> selector
	) {
		_survivorsSelector = requireNonNull(selector);
		return this;
	}

	@SafeVarargs
	public final EngineBuilder<G, C> alterers(final Alterer<G, C>... alterers) {
		_alterer = Alterer.of(alterers);
		return this;
	}

	public EngineBuilder<G, C> optimize(final Optimize optimize) {
		_optimize = requireNonNull(optimize);
		return this;
	}

	public EngineBuilder<G, C> offspringFraction(final double fraction) {
		_offspringFraction = probability(fraction);
		return this;
	}

	public EngineBuilder<G, C> populationSize(final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(format(
				"Population size must be greater than zero, but was %s.", size
			));
		}
		_populationSize = size;
		return this;
	}

	public EngineBuilder<G, C> maximalPhenotypeAge(final int age) {
		if (age < 1) {
			throw new IllegalArgumentException(format(
				"Phenotype age must be greater than one, but was %s.", age
			));
		}
		_maximalPhenotypeAge = age;
		return this;
	}

	public EngineBuilder<G, C> executor(final Executor executor) {
		_executor = requireNonNull(executor);
		return this;
	}

	public EvolutionEngine<G, C> build() {
		return new EvolutionEngine<>(
			_fitnessFunction,
			_fitnessScaler,
			_genotypeFactory,
			_survivorsSelector,
			_offspringSelector,
			_alterer,
			_optimize,
			getOffspringCount(),
			getSurvivorsCount(),
			_maximalPhenotypeAge,
			_executor
		);
	}

	private int getSurvivorsCount() {
		return _populationSize - getOffspringCount();
	}

	private int getOffspringCount() {
		return (int)round(_offspringFraction*_populationSize);
	}

}
