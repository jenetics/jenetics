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

import java.util.HashMap;
import java.util.Map;

import org.jenetics.AbstractAlterer;
import org.jenetics.Alterer;
import org.jenetics.GaussianMutator;
import org.jenetics.MeanAlterer;
import org.jenetics.MultiPointCrossover;
import org.jenetics.Mutator;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.Recombinator;
import org.jenetics.SinglePointCrossover;
import org.jenetics.SwapMutator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class AltererComplexity implements Complexity<Alterer<?, ?>> {

	static final AltererComplexity INSTANCE = new AltererComplexity();

	private static final Map<Class<?>, Complexity<?>> COMP = new HashMap<>();

	static {
		// Alterers
		put(
			Mutator.class,
			a -> 1.0 + baseComplexity(a)
		);
		put(
			SwapMutator.class,
			a -> 1.0 + baseComplexity(a)
		);
		put(
			PartiallyMatchedCrossover.class,
			a -> 1.3 + baseComplexity(a)
		);
		put(
			GaussianMutator.class,
			a -> 1.5 + baseComplexity(a)
		);

		// Recombinators
		put(
			SinglePointCrossover.class,
			a -> 1.0 + recombinationComplexity(a)
		);
		put(
			MultiPointCrossover.class,
			a -> 1.0 + recombinationComplexity(a) + a.getN()*0.2
		);
		put(
			MeanAlterer.class, a -> 1.5 + recombinationComplexity(a)
		);
	}

	private static <T> void put(final Class<T> t, final Complexity<T> c) {
		COMP.put(t, c);
	}

	private static double recombinationComplexity(
		final Recombinator<?, ?> recombinator
	) {
		return recombinator.getOrder() + baseComplexity(recombinator);
	}

	private static double baseComplexity(final AbstractAlterer<?, ?> alterer) {
		return alterer.getProbability()*10;
	}

	private AltererComplexity() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public double complexity(final Alterer<?, ?> alterer) {
		final Complexity<Alterer<?, ?>> complexity =
			(Complexity<Alterer<?, ?>>) COMP.get(alterer.getClass());

		return complexity != null
				? complexity.complexity(alterer)
				: 1.5;
	}
}
