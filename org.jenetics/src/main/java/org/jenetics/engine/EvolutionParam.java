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

import org.jenetics.internal.util.require;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Selector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class EvolutionParam<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final Alterer<G, C> _alterer;
	private final int _offspringCount;
	private final int _survivorsCount;
	private final long _maximalPhenotypeAge;

	private EvolutionParam(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final int survivorsCount,
		final int offspringCount,
		final long maximalPhenotypeAge
	) {
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_offspringCount = require.positive(offspringCount);
		_survivorsCount = require.positive(survivorsCount);
		_maximalPhenotypeAge = require.positive(maximalPhenotypeAge);
	}

	public Alterer<G, C> getAlterer() {
		return _alterer;
	}

	public long getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	public int getOffspringCount() {
		return _offspringCount;
	}

	public Selector<G, C> getOffspringSelector() {
		return _offspringSelector;
	}

	public int getSurvivorsCount() {
		return _survivorsCount;
	}

	public Selector<G, C> getSurvivorsSelector() {
		return _survivorsSelector;
	}

	public int getPopulationSize() {
		return _offspringCount + _survivorsCount;
	}

	public double getOffspringFraction() {
		return (double)getOffspringCount()/(double)getPopulationSize();
	}

	@Override
	public String toString() {
		return _alterer + "\n" +
			_offspringSelector + "\n" +
			_survivorsSelector + "\n" +
			"Offspring fraction = " + getOffspringFraction() + "\n" +
			"Phenotype age = " + _maximalPhenotypeAge + "\n" +
			"Population size = " + getPopulationSize();
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionParam<G, C> of(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final int survivorsCount,
		final int offspringCount,
		final long maximalPhenotypeAge
	) {
		return new EvolutionParam<>(
			survivorsSelector,
			offspringSelector,
			alterer,
			survivorsCount,
			offspringCount,
			maximalPhenotypeAge
		);
	}

}
