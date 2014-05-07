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
package org.jenetics;

import org.jenetics.stat.IntSummary;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class PopulationSummary<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	private final int _count;
	private final Phenotype<G, C> _best;
	private final Phenotype<G, C> _worst;
	private final IntSummary _ageSummary;

	private PopulationSummary(
		final int count,
		final Phenotype<G, C> best,
		final Phenotype<G, C> worst,
		final IntSummary ageSummary
	) {
		_count = count;
		_best = best;
		_worst = worst;
		_ageSummary = ageSummary;
	}

	public int getCount() {
		return _count;
	}

	public Phenotype<G, C> getWorst() {
		return _worst;
	}

	public Phenotype<G, C> getBest() {
		return _best;
	}

	public IntSummary getAgeSummary() {
		return _ageSummary;
	}

}
