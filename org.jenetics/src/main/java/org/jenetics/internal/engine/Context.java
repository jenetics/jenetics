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

import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.jenetics.internal.util.NanoClock;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Selector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-30 $</em>
 */
public class Context<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	// Population
	private final int _populationSize;
	private final int _maximalPhenotypeAge;
	private final double _offspringFraction;
	private final Supplier<Phenotype<G, C>> _phenotype;

	// Selectors
	private final Selector<G, C> _survivorSelector;
	private final Selector<G, C> _offspringSelector;

	// Alterers
	private final Alterer<G, C> _alterer;

	// Optimization.
	private final Optimize _optimize;

	// Execution environment
	private final Clock _clock = NanoClock.INSTANCE;
	private final Executor _executor;



	public Context(
		final int populationSize,
		final int maximalPhenotypeAge,
		final double offspringFraction,
		final Supplier<Phenotype<G, C>> phenotype,
		final Selector<G, C> survivorSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final Optimize optimize,
		final Executor executor
	) {
		_populationSize = populationSize;
		_maximalPhenotypeAge = maximalPhenotypeAge;
		_offspringFraction = offspringFraction;
		_phenotype = phenotype;
		_survivorSelector = survivorSelector;
		_offspringSelector = offspringSelector;
		_alterer = alterer;
		_optimize = optimize;
		_executor = executor;
	}

	public int getPopulationSize() {
		return _populationSize;
	}

	public int getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	public double getOffspringFraction() {
		return _offspringFraction;
	}

	public Supplier<Phenotype<G, C>> getPhenotype() {
		return _phenotype;
	}

	public Selector<G, C> getSurvivorSelector() {
		return _survivorSelector;
	}

	public Selector<G, C> getOffspringSelector() {
		return _offspringSelector;
	}

	public Alterer<G, C> getAlterer() {
		return _alterer;
	}

	public Optimize getOptimize() {
		return _optimize;
	}

	public Clock getClock() {
		return _clock;
	}

	public Executor getExecutor() {
		return _executor;
	}

	public static final class Builder<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		// Population
		private int _populationSize;
		private int _maximalPhenotypeAge;
		private double _offspringFraction;
		private Supplier<Phenotype<G, C>> _phenotype;

		// Selectors
		private Selector<G, C> _survivorSelector;
		private Selector<G, C> _offspringSelector;

		// Alterers
		private Alterer<G, C> _alterer;

		// Optimization.
		private Optimize _optimize;

		// Execution environment
		private Clock _clock = NanoClock.INSTANCE;
		private Executor _executor;
	}


}
