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

import java.time.Clock;
import java.util.concurrent.Executor;

import org.jenetics.internal.util.NanoClock;

import org.jenetics.Gene;
import org.jenetics.Phenotype;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-29 $</em>
 */
public class Context<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
{

	private final Clock _clock = NanoClock.INSTANCE;

	private final int _generation;
	private final int _maximalPhenotypeAge;
	private final int _populationSize;
	private final Factory<Phenotype<G, C>> _phenotypeFactory;
	private final Executor _executor;

	public Context(
		final int generation,
		final int maximalPhenotypeAge,
		final int populationSize,
		final Factory<Phenotype<G, C>> phenotypeFactory,
		final Executor executor
	) {
		_generation = generation;
		_maximalPhenotypeAge = maximalPhenotypeAge;
		_populationSize = populationSize;
		_phenotypeFactory = phenotypeFactory;
		_executor = requireNonNull(executor);
	}

	public Clock getClock() {
		return _clock;
	}

	public int getGeneration() {
		return _generation;
	}

	public int getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	public int getPopulationSize() {
		return _populationSize;
	}

	public Factory<Phenotype<G, C>> getPhenotypeFactory() {
		return _phenotypeFactory;
	}

	public Executor getExecutor() {
		return _executor;
	}
}
