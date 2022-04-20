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
package io.jenetics;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;

import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Represents the result pair of a {@link Alterer#alter(Seq, long)} call, which
 * consists of the altered population and the number of altered individuals.
 *
 * @see Alterer
 *
 * @param population the altered population
 * @param alterations the number of altered individuals
 * @param <G> the gene type
 * @param <C> the result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 4.0
 * @version 7.0
 */
public record AltererResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> (
	ISeq<Phenotype<G, C>> population,
	int alterations
)
	implements Serializable
{
	@Serial
	private static final long serialVersionUID = 2L;

	/**
	 * Create a new alter result for the given arguments.
	 *
	 * @param population the altered population
	 * @param alterations the number of altered individuals
	 * @throws NullPointerException if the given population is {@code null}
	 * @throws IllegalArgumentException if the given {@code alterations} is
	 *         negative
	 */
	public AltererResult {
		Requires.nonNegative(alterations);
		requireNonNull(population);
	}

	/**
	 * Create a new alter result for the given population with zero alterations.
	 *
	 * @param population the altered population
	 * @throws NullPointerException if the given population is {@code null}
	 */
	public AltererResult(ISeq<Phenotype<G, C>> population) {
		this(population, 0);
	}

}
