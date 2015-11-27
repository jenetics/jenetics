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
package org.jenetics.optimizer;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import org.jenetics.GaussianMutator;
import org.jenetics.MeanAlterer;
import org.jenetics.MultiPointCrossover;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.SinglePointCrossover;
import org.jenetics.SwapMutator;
import org.jenetics.engine.EvolutionParam;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Measure<C extends Comparable<? super C>>
	implements Comparable<Measure<C>>
{

	private static final Map<Class<?>, Integer> COMPLEXITIES = new HashMap<>();
	static {
		COMPLEXITIES.put(Mutator.class, 1);
		COMPLEXITIES.put(GaussianMutator.class, 2);
		COMPLEXITIES.put(MeanAlterer.class, 2);
		COMPLEXITIES.put(SwapMutator.class, 2);
		COMPLEXITIES.put(SinglePointCrossover.class, 2);
		COMPLEXITIES.put(MultiPointCrossover.class, 3);
	}

	private final C _comparable;
	private final EvolutionParam<?, C> _params;
	private final Optimize _optimize;

	Measure(
		final C comparable,
		final EvolutionParam<?, C> params,
		final Optimize optimize
	) {
		_comparable = requireNonNull(comparable);
		_params = requireNonNull(params);
		_optimize = requireNonNull(optimize);
	}

	@Override
	public int compareTo(final Measure<C> other) {
		int cmp = _comparable.compareTo(other._comparable);

		// Compare the population size.
		if (cmp == 0) {
			cmp = _optimize.compare(
				other._params.getPopulationSize(),
				_params.getPopulationSize()
			);
		}

		// Compare the alterer complexity.
		if (cmp == 0) {
			cmp = _optimize.compare(
				AltererComplexity.of(other._params.getAlterer()),
				AltererComplexity.of(_params.getAlterer())
			);
		}

		return cmp;
	}

	@Override
	public String toString() {
		return _comparable.toString();
	}

}
