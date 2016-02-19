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

import org.jenetics.DoubleGene;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class OptimizerResult<C extends Comparable<? super C>> {

	private final C _fitness;
	private final EvolutionResult<?, C> _result;
	private final EvolutionParam<?, C> _param;

	private OptimizerResult(
		final C fitness,
		final EvolutionResult<?, C> result,
		final EvolutionParam<?, C> param
	) {
		_fitness = requireNonNull(fitness);
		_result = requireNonNull(result);
		_param = requireNonNull(param);
	}

	public C getFitness() {
		return _fitness;
	}

	public EvolutionResult<?, C> getResult() {
		return _result;
	}

	public EvolutionParam<?, C> getParam() {
		return _param;
	}

	public static <C extends Comparable<? super C>> OptimizerResult<C> of(
		final C fitness,
		final EvolutionResult<?, C> result,
		final EvolutionParam<?, C> param
	) {
		return new OptimizerResult<>(fitness, result, param);
	}

}
