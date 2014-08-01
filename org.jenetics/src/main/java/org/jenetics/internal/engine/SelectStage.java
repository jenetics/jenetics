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

import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletionStage;

import org.jenetics.internal.util.Timer;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * This <i>stage</i> selects the survivor and offspring population.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-01 $</em>
 */
public class SelectStage<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Stage
{

	private final Context<G, C> _context;

	public SelectStage(final Context<G, C> context) {
		super(context.getExecutor());
		_context = context;
	}

	public Result<G, C> select(final Population<G, C> population) {
		final Timer timer = Timer.of(_context.getClock());

		final CompletionStage<Population<G, C>> survivors = async(timer.timing(() ->
				_context.getSurvivorSelector().select(
					population, getSurvivorCount(), _context.getOptimize())
			)
		);

		final CompletionStage<Population<G, C>> offspring = async(timer.timing(() ->
			_context.getOffspringSelector().select(
				population, getOffspringCount(), _context.getOptimize())
			)
		);

		return new Result<>(timer, survivors, offspring);
	}

	private int getSurvivorCount() {
		return _context.getPopulationSize() - getOffspringCount();
	}

	private int getOffspringCount() {
		return (int)round(
			_context.getOffspringFraction()*_context.getPopulationSize()
		);
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
		extends StageResult
	{
		private final CompletionStage<Population<G, C>> _survivors;
		private final CompletionStage<Population<G, C>> _offspring;

		private Result(
			final Timer timer,
			final CompletionStage<Population<G, C>> survivors,
			final  CompletionStage<Population<G, C>> offspring
		) {
			super(timer);
			_survivors = requireNonNull(survivors);
			_offspring = requireNonNull(offspring);
		}

		public CompletionStage<Population<G, C>> getSurvivors() {
			return _survivors;
		}

		public CompletionStage<Population<G, C>> getOffspring() {
			return _offspring;
		}

	}

}
