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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.Selector;

/**
 * This <i>stage</i> selects the survivor and offspring population.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-05 $</em>
 */
public class SelectStage<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Stage
{

	private final Selector<G, C> _selector;
	private final int _count;
	private final Optimize _optimize;


	public SelectStage(
		final Selector<G, C> selector,
		final int count,
		final Optimize optimize,
		final Executor executor
	) {
		super(executor);
		_selector = selector;
		_count = count;
		_optimize = optimize;
	}

	public CompletionStage<TimedResult<Population<G, C>>>
	select(final Population<G, C> population) {
		return async(TimedResult.of(() ->
			_selector.select(population, _count, _optimize)
		));
	}

}
