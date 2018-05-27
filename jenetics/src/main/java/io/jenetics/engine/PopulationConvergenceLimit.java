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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.stat.DoubleMoments;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.2
 * @since 3.9
 */
final class PopulationConvergenceLimit<N extends Number & Comparable<? super N>>
	implements Predicate<EvolutionResult<?, N>>
{

	private final BiPredicate<Double, DoubleMoments> _proceed;

	PopulationConvergenceLimit(final BiPredicate<Double, DoubleMoments> proceed) {
		_proceed = requireNonNull(proceed);
	}

	@Override
	public boolean test(final EvolutionResult<?, N> result) {
		final DoubleMomentStatistics fitness = new DoubleMomentStatistics();
		result.getPopulation()
			.forEach(p -> fitness.accept(p.getFitness().doubleValue()));

		return result.getTotalGenerations() <= 1 ||
			_proceed.test(
				result.getBestFitness() != null
					? result.getBestFitness().doubleValue()
					: Double.NaN,
				DoubleMoments.of(fitness)
			);
	}

}
