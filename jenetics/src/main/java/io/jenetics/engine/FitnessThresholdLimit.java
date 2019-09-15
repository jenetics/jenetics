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

import java.util.function.Predicate;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version 4.2
 */
final class FitnessThresholdLimit<C extends Comparable<? super C>>
	implements Predicate<EvolutionResult<?, C>>
{

	private final C _threshold;

	private boolean _proceed = true;

	FitnessThresholdLimit(final C threshold) {
		_threshold = requireNonNull(threshold);
	}

	@Override
	public boolean test(final EvolutionResult<?, C> result) {
		final boolean proceed =
			_proceed &&
			result.getOptimize().compare(_threshold, result.getBestFitness()) >= 0;

		try {
			return _proceed;
		} finally {
			_proceed = proceed;
		}
	}

}
