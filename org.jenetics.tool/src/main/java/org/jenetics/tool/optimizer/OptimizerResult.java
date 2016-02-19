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
import org.jenetics.Selector;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class OptimizerResult<C extends Comparable<? super C>>
	implements Comparable<OptimizerResult<C>>
{

	private final EvolutionResult<?, C> _result;
	private final EvolutionParam<?, C> _param;

	private OptimizerResult(
		final EvolutionResult<?, C> result,
		final EvolutionParam<?, C> param
	) {
		_result = requireNonNull(result);
		_param = requireNonNull(param);
	}

	public C getFitness() {
		return _result.getBestFitness();
	}

	public EvolutionResult<?, C> getResult() {
		return _result;
	}

	public EvolutionParam<?, C> getParam() {
		return _param;
	}

	@Override
	public int compareTo(final OptimizerResult<C> other) {
		int cmp = getFitness().compareTo(other.getFitness());
		if (cmp == 0) {
			cmp = _result.getOptimize().compare(
				other.getParam().getPopulationSize(),
				getParam().getPopulationSize()
			);
		}

		if (cmp == 0) {
			final double complexity1 =
				complexity(getParam().getAlterer()) +
				complexity(getParam().getOffspringSelector())*0.5 +
				complexity(getParam().getSurvivorsSelector())*0.5;

			final double complexity2 =
				complexity(other.getParam().getAlterer()) +
				complexity(other.getParam().getOffspringSelector())*0.5 +
				complexity(other.getParam().getSurvivorsSelector())*0.5;

			cmp = _result.getOptimize().compare(complexity2, complexity1);
		}

		return cmp;
	}

	private static double complexity(final Alterer<?, ?> alterer) {
		return AltererComplexity.INSTANCE.complexity(alterer);
	}

	private static double complexity(final Selector<?, ?> selector) {
		return SelectorComplexity.INSTANCE.complexity(selector);
	}

	public static <C extends Comparable<? super C>> OptimizerResult<C> of(
		final EvolutionResult<?, C> result,
		final EvolutionParam<?, C> param
	) {
		return new OptimizerResult<>(result, param);
	}

}
