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

import java.util.concurrent.CompletionStage;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-30 $</em>
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

	public Result select(final Population<G, C> population) {
		return new Result() {{
			survivors = async(timing(() ->
				_context.getSurvivorSelector()
					.select(population, getSurvivorCount(), _context.getOptimize()))
			);
			offspring = async(timing(() ->
				_context.getOffspringSelector()
					.select(population, getOffspringCount(), _context.getOptimize()))
			);
		}};
	}

	private int getSurvivorCount() {
		return _context.getPopulationSize() - getOffspringCount();
	}

	private int getOffspringCount() {
		return (int)round(
			_context.getOffspringFraction()*_context.getPopulationSize()
		);
	}

	public abstract class Result extends StageResult {
		public CompletionStage<Population<G, C>> survivors;
		public CompletionStage<Population<G, C>> offspring;
	}

}
