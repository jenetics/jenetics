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

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.ObjectRef;

import org.jenetics.Gene;
import org.jenetics.Phenotype;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class CombineStage<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private final Context<G, C> _context;

	public CombineStage(final Context<G, C> context) {
		_context = requireNonNull(context);
	}

	public CompletionStage<Result<CombineResult<G, C>>> combine(
		final Population<G, C> survivors,
		final Population<G, C> offspring
	) {
		final Population<G, C> population = new Population<>(_context.getPopulationSize());

		survivors.stream()
			.filter(s -> s.getAge(_context.getGeneration()) <= _context.getMaximalPhenotypeAge())
			.filter(s -> s.isValid());

		final CompletionStage<CombineResult<G, C>> filter =
			CompletableFuture.supplyAsync(() -> filter(survivors), _context.getExecutor());

		filter.thenAcceptAsync(s -> population.addAll(s.getPopulation()), _context.getExecutor());

		final CompletionStage<Void> off =
			CompletableFuture.runAsync(() -> population.addAll(offspring), _context.getExecutor());


		return null;
	}

	private CombineResult<G, C> filter(final Population<G, C> survivors) {
		int invalid = 0;
		int killed = 0;

		for (int i = 0, n = survivors.size(); i < n; ++i) {
			final Phenotype<G, C> survivor = survivors.get(i);

			final boolean isTooOld =
				survivor.getAge(_context.getGeneration()) >
					_context.getMaximalPhenotypeAge();

			final boolean isInvalid = isTooOld || !survivor.isValid();

			// Sorry, too old or not valid.
			if (isInvalid) {
				survivors.set(i, _context.getPhenotypeFactory().newInstance());
			}

			if (isTooOld) ++killed;
			else if (isInvalid) ++invalid;
		}

		return new CombineResult<>(survivors, invalid, killed);
	}


	public static final class CombineResult<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		private final Population<G, C> _population;
		private final int _invalid;
		private final int _killed;

		private CombineResult(
			final Population<G, C> population,
			final int invalid,
			final int killed
		) {
			_population = population;
			_invalid = invalid;
			_killed = killed;
		}

		public Population<G, C> getPopulation() {
			return _population;
		}

		public int getInvalid() {
			return _invalid;
		}

		public int getKilled() {
			return _killed;
		}

	}

}
