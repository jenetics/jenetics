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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.tool;

import static java.lang.Math.max;

import io.jenetics.Gene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.TournamentSelector;
import io.jenetics.TruncationSelector;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
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
	public ISeq<Phenotype<G, C>> select(
		final Seq<Phenotype<G, C>> population,
		final int count,
		final Optimize opt
	) {
		return population.isEmpty() || count <= 0
			? ISeq.empty()
			: _elitist.select(population, 1, opt)
				.append(_rest.select(population, max(0, count - 1), opt));
	}

}
