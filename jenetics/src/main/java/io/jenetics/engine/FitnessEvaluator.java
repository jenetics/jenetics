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

import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.internal.concurrent.RunnableFunction;
import io.jenetics.util.BatchExecutor;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class FitnessEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>
{

	private final Function<? super Genotype<G>, ? extends C> _function;
	private final BatchExecutor _executor;

	public FitnessEvaluator(
		final Function<? super Genotype<G>, ? extends C> function,
		final BatchExecutor executor
	) {
		_function = requireNonNull(function);
		_executor = requireNonNull(executor);
	}

	public Function<? super Genotype<G>, ? extends C> function() {
		return _function;
	}

	@Override
	public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
		final var tasks = population.stream()
			.filter(Phenotype::nonEvaluated)
			.map(phenotype -> new RunnableFunction<>(
				phenotype,
				_function.compose(Phenotype::genotype))
			)
			.collect(ISeq.toISeq());

		final ISeq<Phenotype<G, C>> result;
		if (tasks.nonEmpty()) {
			_executor.execute(tasks);

			result = tasks.size() == population.size()
				? tasks.map(t -> t.input().withFitness(t.result()))
				: population.stream()
					.filter(Phenotype::isEvaluated)
					.collect(ISeq.toISeq())
					.append(tasks.map(t -> t.input().withFitness(t.result())));
		} else {
			result = population.asISeq();
		}

		return result;
	}
}
