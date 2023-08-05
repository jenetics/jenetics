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
package io.jenetics.internal.concurrent;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Evaluator;
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
public abstract class AbstractEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>
{

	protected final Function<? super Genotype<G>, ? extends C> _function;

	protected AbstractEvaluator(
		final Function<? super Genotype<G>, ? extends C> function
	) {
		_function = requireNonNull(function);
	}

	@Override
	public final ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
		final var tasks = population.stream()
			.filter(Phenotype::nonEvaluated)
			.map(pt -> new FitnessCalculationTask<>(pt, _function))
			.collect(ISeq.toISeq());

		final ISeq<Phenotype<G, C>> result;
		if (tasks.nonEmpty()) {
			execute(tasks);

			result = tasks.size() == population.size()
				? tasks.map(FitnessCalculationTask::phenotype)
				: population.stream()
					.filter(Phenotype::isEvaluated)
					.collect(ISeq.toISeq())
					.append(tasks.map(FitnessCalculationTask::phenotype));
		} else {
			result = population.asISeq();
		}

		return result;
	}

	protected abstract void execute(final Seq<? extends Runnable> tasks);

}
