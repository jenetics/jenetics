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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class BatchEval<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>, Function<Genotype<G>, C>
{

	private final Function<Seq<Genotype<G>>, Seq<C>> _fitness;

	private final Map<Genotype<G>, C> _values = new HashMap<>();

	public BatchEval(final Function<Seq<Genotype<G>>, Seq<C>> fitness) {
		_fitness = fitness;
	}

	@Override
	public void evaluate(final ISeq<Phenotype<G, C>> population) {
		_values.clear();

		final ISeq<Genotype<G>> pop = population.stream()
			.filter(pt -> !pt.isEvaluated())
			.map(Phenotype::getGenotype)
			.collect(ISeq.toISeq());

		final Seq<C> results = _fitness.apply(pop);
		assert results.length() == pop.length();

		for (int i = 0; i < results.length(); ++i) {
			_values.put(pop.get(i), results.get(i));
		}
	}

	@Override
	public C apply(final Genotype<G> gt) {
		final C result = _values.get(gt);
		if (result == null) {
			throw new IllegalStateException();

		}
		return result;
	}
}
