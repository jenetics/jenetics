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

import org.jenetics.internal.util.require;

import org.jenetics.Gene;
import org.jenetics.Selector;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Selectors {

	private Selectors() {require.noInstance();}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	ISeq<Proxy<Selector<G, C>>> generic() {
		return ISeq.of(
			new ExponentialRankSelectorProxy<G, C>(1.0),
			new LinearRankSelectorProxy<G, C>(1.0),
			new TournamentSelectorProxy<G, C>(1.0, 20),
			new TruncationSelectorProxy<G, C>(1.0)
		);
	}

	public static <G extends Gene<?, G>, C extends Number & Comparable<? super C>>
	ISeq<Proxy<Selector<G, C>>> number() {
		return ISeq.of(
			new BoltzmannSelectorProxy<G, C>(1.0),
			new ExponentialRankSelectorProxy<G, C>(1.0),
			new LinearRankSelectorProxy<G, C>(1.0),
			new RouletteWheelSelectorProxy<G, C>(1),
			new StochasticUniversalSelectorProxy<G, C>(1.0),
			new TournamentSelectorProxy<G, C>(1.0, 20),
			new TruncationSelectorProxy<G, C>(1.0)
		);
	}

}
