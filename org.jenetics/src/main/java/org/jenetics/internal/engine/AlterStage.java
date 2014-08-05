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

import org.jenetics.internal.util.Timer;

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

	private final Context<G, C> _context;

	public AlterStage(final Context<G, C> context) {
		super(context.getExecutor());
		_context = context;
	}

	public Result<G, C> alter(final Population<G, C> offspring, final int generation) {
//		final Timer timer = Timer.of(_context.getClock());
//
//		final CompletionStage<Integer> altered = async(timer.timing(() ->
//			_context.getAlterer().alter(offspring, generation)
//		));
//
//		return new Result<>(timer, altered);

		return null;
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
		private final CompletionStage<Integer> _altered;

		private Result(
			final Timer timer,
			final  CompletionStage<Integer> altered
		) {
			super(timer);
			_altered = requireNonNull(altered);
		}

		public CompletionStage<Integer> getOffspring() {
			return _altered;
		}

	}

}
