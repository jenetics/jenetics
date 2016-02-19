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

import java.util.Comparator;

import org.jenetics.Alterer;
import org.jenetics.Optimize;
import org.jenetics.Selector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class OptimizerResultComparator<C extends Comparable<? super C>>
	implements Comparator<OptimizerResult<C>>
{

	private final Optimize _optimize;

	OptimizerResultComparator(final Optimize optimize) {
		_optimize = requireNonNull(optimize);
	}

	@Override
	public int compare(final OptimizerResult<C> that, final OptimizerResult<C> other) {
		int cmp = that.getFitness().compareTo(other.getFitness());
		if (cmp == 0) {
			cmp = _optimize.compare(
				other.getParam().getPopulationSize(),
				that.getParam().getPopulationSize()
			);
		}

		if (cmp == 0) {
			final double complexity1 =
				complexity(that.getParam().getAlterer()) +
				complexity(that.getParam().getOffspringSelector())*0.5 +
				complexity(that.getParam().getSurvivorsSelector())*0.5;

			final double complexity2 =
				complexity(other.getParam().getAlterer()) +
				complexity(other.getParam().getOffspringSelector())*0.5 +
				complexity(other.getParam().getSurvivorsSelector())*0.5;

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

}
