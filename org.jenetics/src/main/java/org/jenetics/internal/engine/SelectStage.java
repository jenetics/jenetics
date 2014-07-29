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

import static org.jenetics.internal.util.NanoClock.timing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.Selector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-29 $</em>
 */
public class SelectStage<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	public int survivors;
	public int offspring;
	private Selector<G, C> survivorSelector;
	private Selector<G, C> offspringSelector;
	public Optimize optimize;
	public Executor executor;

	public final AtomicLong time = new AtomicLong(0);

	public Result select(final Population<G, C> population) {
		return new Result(
			CompletableFuture.supplyAsync(timing(time, () ->
				survivorSelector.select(population, survivors, optimize)),
				executor
			),
			CompletableFuture.supplyAsync(timing(time, () ->
				offspringSelector.select(population, offspring, optimize)),
				executor
			)
		);
	}

	public final class Result {
		public final CompletionStage<Population<G, C>> survivors;
		public final CompletionStage<Population<G, C>> offspring;

		private Result(
			final CompletionStage<Population<G, C>> survivors,
			final CompletionStage<Population<G, C>> offspring
		) {
			this.survivors = survivors;
			this.offspring = offspring;
		}
	}

}
