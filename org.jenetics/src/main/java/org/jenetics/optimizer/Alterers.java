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

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.NumericGene;
import org.jenetics.util.ISeq;
import org.jenetics.util.Mean;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Alterers {

	private Alterers() {require.noInstance();}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	ISeq<Proxy<Alterer<G, C>>> general() {
		return ISeq.of(
			new MultiPointCrossoverProxy<G, C>(0.5, 2, 10),
			new SinglePointCrossoverProxy<G, C>(0.5),
			new MutatorProxy<G, C>(0.5),
			new SwapMutatorProxy<G, C>(0.5)
		);
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	ISeq<Proxy<Alterer<G, C>>> numeric() {
		return ISeq.of(
			new GaussianMutatorProxy<G, C>(0.5),
			new MultiPointCrossoverProxy<G, C>(0.5, 2, 10),
			new SinglePointCrossoverProxy<G, C>(0.5),
			new MutatorProxy<G, C>(0.5),
			new SwapMutatorProxy<G, C>(0.5)
		);
	}

	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	ISeq<Proxy<Alterer<G, C>>> mean() {
		return ISeq.of(
			new MeanAltererProxy<G, C>(0.5),
			new MultiPointCrossoverProxy<G, C>(0.5, 2, 10),
			new SinglePointCrossoverProxy<G, C>(0.5),
			new MutatorProxy<G, C>(0.5),
			new SwapMutatorProxy<G, C>(0.5)
		);
	}

	public static <G extends NumericGene<?, G> & Mean<G>, C extends Comparable<? super C>>
	ISeq<Proxy<Alterer<G, C>>> numericMean() {
		return ISeq.of(
			new GaussianMutatorProxy<G, C>(0.5),
			new MeanAltererProxy<G, C>(0.5),
			new MultiPointCrossoverProxy<G, C>(0.5, 2, 10),
			new SinglePointCrossoverProxy<G, C>(0.5),
			new MutatorProxy<G, C>(0.5),
			new SwapMutatorProxy<G, C>(0.5)
		);
	}

}
