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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-05 $</em>
 */
public class AlterStage<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
	extends Stage
{

	private final Alterer<G, C> _alterer;

	public AlterStage(final Alterer<G, C> alterer, final Executor executor) {
		super(executor);
		_alterer = alterer;
	}

	public CompletionStage<TimedResult<Result<G, C>>>
	alter(final Population<G, C> population, final int generation) {
		return async(TimedResult.of(() ->
			new Result<>(population, _alterer.alter(population, generation))
		));
	}

	/**
	 * Contains the <i>asynchronous</i> result of the selection stage.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 */
	public static final class Result<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		private final Population<G, C> _population;
		private final int _altered;

		private Result(
			final Population<G, C> population,
			final int altered
		) {
			_population = requireNonNull(population);
			_altered = altered;
		}

		public Population<G, C> getPopulation() {
			return _population;
		}

		public int getAltered() {
			return _altered;
		}

	}

}
