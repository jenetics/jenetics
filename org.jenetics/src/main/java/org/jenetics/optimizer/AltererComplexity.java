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
import org.jenetics.Recombinator;
import org.jenetics.SinglePointCrossover;
import org.jenetics.SwapMutator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class AltererComplexity {

	private static final
	Map<Class<?>, Complexity<?>> COMPLEXITIES = new HashMap<>();

	static {
		// Alterers
		put(Mutator.class, a -> 1.0 + alt(a));
		put(SwapMutator.class, a -> 1.0 + alt(a));
		put(GaussianMutator.class, a -> 1.5 + alt(a));

		// Recombinators
		put(SinglePointCrossover.class, a -> 1.0 + comb(a));
		put(MultiPointCrossover.class, a -> 1.0 + comb(a) + a.getN()*0.2);
		put(MeanAlterer.class, a -> 1.5 + comb(a));
	}

	private static <T> void put(final Class<T> t, final Complexity<T> c) {
		COMPLEXITIES.put(t, c);
	}

	private static double comb(final Recombinator<?, ?> alterer) {
		return alterer.getOrder() + alt(alterer);
	}

	private static double alt(final AbstractAlterer<?, ?> alterer) {
		return alterer.getProbability()*10;
	}

	@SuppressWarnings("unchecked")
	public static double of(final Alterer<?, ?> alterer) {
		final Complexity<Alterer<?, ?>> complexity =
			(Complexity<Alterer<?, ?>>)COMPLEXITIES.get(alterer.getClass());

		return (complexity != null ? complexity : (Complexity<Alterer<?, ?>>)a -> 1.5)
			.complexity(alterer);
	}

}
