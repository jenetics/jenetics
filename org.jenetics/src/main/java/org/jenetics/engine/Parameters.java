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

import java.io.Serializable;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Selector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Parameters<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final  Alterer<G, C> _alterer;
	private final Selector<G, C> _offspringSelector;
	private final Selector<G, C> _survivorsSelector;
	private final double _offspringFraction;
	private final int _populationSize;
	private final long _maximalPhenotypeAge;

	private Parameters(
		final Alterer<G, C> alterer,
		final Selector<G, C> offspringSelector,
		final Selector<G, C> survivorsSelector,
		final double offspringFraction,
		final int populationSize,
		final long maximalPhenotypeAge
	) {
		_alterer = requireNonNull(alterer);
		_offspringSelector = requireNonNull(offspringSelector);
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringFraction = offspringFraction;
		_populationSize = populationSize;
		_maximalPhenotypeAge = maximalPhenotypeAge;
	}

	public Alterer<G, C> getAlterer() {
		return _alterer;
	}

	public Selector<G, C> getOffspringSelector() {
		return _offspringSelector;
	}

	public Selector<G, C> getSurvivorsSelector() {
		return _survivorsSelector;
	}

	public double getOffspringFraction() {
		return _offspringFraction;
	}

	public int getPopulationSize() {
		return _populationSize;
	}

	public long getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Parameters<G, C> of(
		final Alterer<G, C> alterer,
		final Selector<G, C> offspringSelector,
		final Selector<G, C> survivorsSelector,
		final double offspringFraction,
		final int populationSize,
		final long maximalPhenotypeAge
	) {
		return new Parameters<>(
			alterer,
			offspringSelector,
			survivorsSelector,
			offspringFraction,
			populationSize,
			maximalPhenotypeAge
		);
	}

}
