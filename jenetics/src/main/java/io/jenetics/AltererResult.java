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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Represents the result pair of a {@link Alterer#alter(Seq, long)} call, which
 * consists of the altered population and the number of altered individuals.
 *
 * @see Alterer
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 4.0
 */
public final /*record*/ class AltererResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final ISeq<Phenotype<G, C>> _population;
	private final int _alterations;

	private AltererResult(
		final Seq<Phenotype<G, C>> population,
		final int alterations
	) {
		if (alterations < 0) {
			throw new IllegalArgumentException(
				"Alterations must not be negative: " + alterations
			);
		}

		_population = requireNonNull(population).asISeq();
		_alterations = alterations;
	}

	/**
	 * Return the altered population.
	 *
	 * @return the altered population
	 */
	public ISeq<Phenotype<G, C>> population() {
		return _population;
	}

	/**
	 * Return the number of altered individuals.
	 *
	 * @return the number of altered individuals
	 */
	public int alterations() {
		return _alterations;
	}

	@Override
	public int hashCode() {
		return
			hash(_population,
			hash(_alterations));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof AltererResult<?, ?> &&
			_alterations == ((AltererResult<?, ?>)obj)._alterations &&
			_population.equals(((AltererResult<?, ?>)obj)._population);
	}

	@Override
	public String toString() {
		return format("[%s, %s]", _population, _alterations);
	}

	/**
	 * Return a new alter result for the given arguments.
	 *
	 * @param population the altered population
	 * @param alterations the number of altered individuals
	 * @param <G> the gene type
	 * @param <C> the result type
	 * @return a new alterer for the given arguments
	 * @throws NullPointerException if the given population is {@code null}
	 * @throws IllegalArgumentException if the given {@code alterations} is
	 *         negative
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererResult<G, C> of(
		final Seq<Phenotype<G, C>> population,
		final int alterations
	) {
		return new AltererResult<>(population, alterations);
	}

	/**
	 * Return a new alter result for the given arguments.
	 *
	 * @param population the altered population
	 * @param <G> the gene type
	 * @param <C> the result type
	 * @return a new alterer for the given arguments
	 * @throws NullPointerException if the given population is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererResult<G, C>
	of(final Seq<Phenotype<G, C>> population) {
		return new AltererResult<>(population, 0);
	}

}
