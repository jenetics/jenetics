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
package org.jenetics.tool;

import static java.lang.Math.max;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class ElitistSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{
	private final TruncationSelector<G, C> _elitist = new TruncationSelector<>();
	private final TournamentSelector<G, C> _rest = new TournamentSelector<>(3);

	@Override
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		return population.isEmpty() || count <= 0
			? new Population<>(0)
			: append(
				_elitist.select(population, 1, opt),
				_rest.select(population, max(0, count - 1), opt));
	}

	private Population<G, C> append(
		final Population<G, C> p1,
		final Population<G, C> p2
	) {
		p1.addAll(p2);
		return p1;
	}
}
