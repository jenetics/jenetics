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

import java.io.Serializable;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Represents the result pair of a {@link Alterer#alter(Seq, long)} call, which
 * consists of the altered population and the number of altered individuals.
 *
 * @see Alterer
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 4.0
 */
public final class AlterResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final ISeq<Phenotype<G, C>> _population;
	private final int _alterations;

	private AlterResult(
		final ISeq<Phenotype<G, C>> population,
		final int alterations
	) {
		if (alterations < 0) {
			throw new IllegalArgumentException(
				"Alterations is negative: " + alterations
			);
		}

		_population = requireNonNull(population);
		_alterations = alterations;
	}

	/**
	 * Return the altered population.
	 *
	 * @return the altered population
	 */
	public ISeq<Phenotype<G, C>> getPopulation() {
		return _population;
	}

	/**
	 * Return the number of altered individuals.
	 *
	 * @return the number of altered individuals
	 */
	public int getAlterations() {
		return _alterations;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*_population.hashCode() + 37;
		hash += 31*_alterations + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AlterResult<?, ?> &&
			_alterations == ((AlterResult)obj)._alterations &&
			_population.equals(((AlterResult)obj)._population);
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
	AlterResult<G, C> of(
		final ISeq<Phenotype<G, C>> population,
		final int alterations
	) {
		return new AlterResult<>(population, alterations);
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
	AlterResult<G, C> of(final ISeq<Phenotype<G, C>> population) {
		return new AlterResult<>(population, 0);
	}

}
