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

import java.io.Serializable;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * Represents the result of the alter step.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
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

	AlterResult(
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

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AlterResult<G, C> of(
		final Population<G, C> population,
		final int alterCount
	) {
		return new AlterResult<>(population, alterCount);
	}
}
