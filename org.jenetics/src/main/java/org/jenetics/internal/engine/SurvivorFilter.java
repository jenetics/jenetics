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

import org.jenetics.Gene;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-29 $</em>
 */
public class SurvivorFilter<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	private int _generation;
	private int _maxAge;
	private final Factory<Phenotype<G, C>> _factory;

	public SurvivorFilter(
		final int generation,
		final int maxAge,
		final Factory<Phenotype<G, C>> factory
	) {
		_generation = generation;
		_maxAge = maxAge;
		_factory = factory;
	}


	public final class Result {
		public final Population<G, C> population;
		public final int invalid;
		public final int killed;


		private Result(final Population<G, C> population, final int invalid, final int killed) {
			this.population = population;
			this.invalid = invalid;
			this.killed = killed;
		}
	}

}
