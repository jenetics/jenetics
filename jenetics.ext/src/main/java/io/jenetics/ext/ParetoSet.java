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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
public class ParetoSet<G extends Gene<?, G>, T> {

	final ISeq<Phenotype<G, MOF<T>>> _elements;

	public ParetoSet(final ISeq<Phenotype<G, MOF<T>>> elements) {
		_elements = requireNonNull(elements);
	}

	public static <G extends Gene<?, G>, T>
	Collector<EvolutionResult<G, MOF<T>>, ?, ParetoSet<G, T>>
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


	static  <C extends Comparable<? super C>> List<C>
	Pareto(final List<C> population) {
		// find non-dominated that aren't part of previous front
		final List<C> notDominated = new ArrayList<C>();

		for (C s1 : population) { // determine if s1 is dominated
			boolean dominated = false;

			for (C s2 : population) {
				if (s1.compareTo(s2) == 0) {
				//if (doesDominate(s1, s2)) {
					dominated = true;
					break;
				}
			}

			if (!dominated) {
				notDominated.add(s1);
			}
		}

		return notDominated;
	}

	static  <C extends Comparable<? super C>> ISeq<C>
	pareto(final Seq<C> elements) {
		Seq<C> pareto = MSeq.of(elements).sort(Comparator.reverseOrder());
		System.out.println("U: " + elements);
		System.out.println("S: " + pareto);

		Iterator<C> it = pareto.iterator();
		while (it.hasNext()) {
			final Seq<C> pareto0 = pareto0(it.next(), pareto);

			if (pareto0.size() < pareto.size()) {
				it = pareto0.iterator();
			}
			pareto = pareto0;
		}

		return pareto.asISeq();
	}

	static  <C extends Comparable<? super C>> Seq<C>
	pareto0(final C point, final Seq<C> elements) {
		return elements.stream()
			.filter(pt -> point.compareTo(pt) <= 0)
			.collect(Seq.toSeq());
	}



	static  <G extends Gene<?, G>, T> ISeq<Phenotype<G, MOF<T>>>
	merge(final ISeq<Phenotype<G, MOF<T>>> a, final ISeq<Phenotype<G, MOF<T>>> b) {
		return ISeq.empty();
	}

}
