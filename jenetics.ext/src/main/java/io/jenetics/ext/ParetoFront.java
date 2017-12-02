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
package io.jenetics.ext;

import static java.util.Objects.requireNonNull;

import java.util.stream.Collector;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ParetoFront<G extends Gene<?, G>, T> {

	final ISeq<Phenotype<G, MOF<T>>> _elements;

	public ParetoFront(final ISeq<Phenotype<G, MOF<T>>> elements) {
		_elements = requireNonNull(elements);
	}

	public static <G extends Gene<?, G>, T>
	Collector<EvolutionResult<G, MOF<T>>, ?, ParetoFront<G, T>>
	toParetoSet() {
		return null;
		/*
		return Collector.of(
			MinMax::<EvolutionResult<G, C>>of,
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax() != null
				? mm.getMax().withTotalGenerations(mm.getCount())
				: null
		);*/
	}


	/*
	 * Reference: E. Zitzler and L. Thiele
	 * Multiobjective Evolutionary Algorithms: A Comparative Case Study and the Strength Pareto Approach,
	 * IEEE Transactions on Evolutionary Computation, vol. 3, no. 4,
	 * pp. 257-271, 1999.
	 */
	static  <C extends Comparable<? super C>> ISeq<C>
	pareto(final Seq<C> elements) {
		final MSeq<C> front = MSeq.of(elements);

		int n = elements.size();
		int i = 0;
		while (i < n) {
			int j = i + 1;
			while (j < n) {
				if (front.get(i).compareTo(front.get(j)) > 0) {
					n--;
					front.swap(j, n);
				} else if (front.get(j).compareTo(front.get(i)) > 0) {
					n--;
					front.swap(i, n);
					i--;
					break;
				} else {
					j++;
				}
			}
			i++;
		}

		return front.subSeq(0, n).copy().toISeq();
	}

	static  <G extends Gene<?, G>, T> ISeq<Phenotype<G, MOF<T>>>
	merge(final ISeq<Phenotype<G, MOF<T>>> a, final ISeq<Phenotype<G, MOF<T>>> b) {
		return ISeq.empty();
	}

}
