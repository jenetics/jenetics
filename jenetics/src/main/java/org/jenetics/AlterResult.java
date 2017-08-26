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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * Represents the result of a {@link Alterer#alter(Seq, long)} call.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class AlterResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	private final ISeq<Phenotype<G, C>> _population;
	private final int _alterations;

	private AlterResult(
		final ISeq<Phenotype<G, C>> population,
		final int alterations
	) {
		_population = requireNonNull(population);
		_alterations = alterations;
	}

	public ISeq<Phenotype<G, C>> getPopulation() {
		return _population;
	}

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

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AlterResult<G, C> of(
		final ISeq<Phenotype<G, C>> population,
		final int alterations
	) {
		return new AlterResult<>(population, alterations);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AlterResult<G, C> of(final ISeq<Phenotype<G, C>> population) {
		return new AlterResult<>(population, 0);
	}

}
