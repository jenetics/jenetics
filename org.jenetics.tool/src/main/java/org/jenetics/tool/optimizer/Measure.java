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
package org.jenetics.tool.optimizer;

import static java.util.Objects.requireNonNull;

import org.jenetics.Alterer;
import org.jenetics.Optimize;
import org.jenetics.Selector;
import org.jenetics.engine.EvolutionParam;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Measure<C extends Comparable<? super C>>
	implements Comparable<Measure<C>>
{

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
		requireNonNull(other);

		int cmp = _comparable.compareTo(other._comparable);
		if (cmp == 0) {
			cmp = _optimize.compare(
				other._params.getPopulationSize(),
				_params.getPopulationSize()
			);
		}

		if (cmp == 0) {
			final double complexity1 =
				complexity(_params.getAlterer()) +
				complexity(_params.getOffspringSelector())*0.5 +
				complexity(_params.getSurvivorsSelector())*0.5;

			final double complexity2 =
				complexity(other._params.getAlterer()) +
				complexity(other._params.getOffspringSelector())*0.5 +
				complexity(other._params.getSurvivorsSelector())*0.5;

			cmp = _optimize.compare(complexity2, complexity1);
		}

		return cmp;
	}

	private static double complexity(final Alterer<?, ?> alterer) {
		return AltererComplexity.INSTANCE.complexity(alterer);
	}

	private static double complexity(final Selector<?, ?> selector) {
		return SelectorComplexity.INSTANCE.complexity(selector);
	}

	@Override
	public String toString() {
		return _comparable.toString();
	}

}
