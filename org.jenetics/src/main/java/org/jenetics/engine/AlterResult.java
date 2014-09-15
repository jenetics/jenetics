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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * Represents the result of the alter step.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class AlterResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Population<G, C> _population;
	private final int _alterCount;

	private AlterResult(
		final Population<G, C> population,
		final int alterCount
	) {
		_population = requireNonNull(population);
		_alterCount = alterCount;
	}

	public Population<G, C> getPopulation() {
		return _population;
	}

	public int getAlterCount() {
		return _alterCount;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_population)
			.and(_alterCount).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(result ->
			eq(_population, result._population) &&
			eq(_alterCount, result._alterCount)
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AlterResult<G, C> of(
		final Population<G, C> population,
		final int alterCount
	) {
		return new AlterResult<>(population, alterCount);
	}
}
