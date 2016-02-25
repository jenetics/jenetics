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

import java.util.HashMap;
import java.util.Map;

import org.jenetics.BoltzmannSelector;
import org.jenetics.ExponentialRankSelector;
import org.jenetics.LinearRankSelector;
import org.jenetics.MonteCarloSelector;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.Selector;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class SelectorComplexity implements Complexity<Selector<?, ?>> {

	static final SelectorComplexity INSTANCE = new SelectorComplexity();

	private static final Map<Class<?>, Complexity<?>> COMP = new HashMap<>();

	static {
		put(MonteCarloSelector.class, s -> 0.75);
		put(BoltzmannSelector.class, s -> 3);
		put(ExponentialRankSelector.class, s -> 2);
		put(LinearRankSelector.class, s -> 1.5);
		put(RouletteWheelSelector.class, s -> 1.5);
		put(StochasticUniversalSelector.class, s -> 1.3);
		put(TournamentSelector.class, s -> 1 + s.getSampleSize()*0.15);
		put(TruncationSelector.class, a -> 1.75);
	}

	private static <T> void put(final Class<T> t, final Complexity<T> c) {
		COMP.put(t, c);
	}

	private SelectorComplexity() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public double complexity(final Selector<?, ?> selector) {
		final Complexity<Selector<?, ?>> complexity =
			(Complexity<Selector<?, ?>>) COMP.get(selector.getClass());

		return complexity != null
			? complexity.complexity(selector)
			: 1.5;
	}

}
