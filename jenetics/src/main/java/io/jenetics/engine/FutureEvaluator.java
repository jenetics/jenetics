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

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * Example of an {@code Engine.Evaluator} where the fitness function returns
 * a {@link Future} of the fitness value instead the value itself.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class FutureEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>
{
	private final Function<? super Genotype<G>, ? extends Future<C>> _fitness;

	FutureEvaluator(
		final Function<? super Genotype<G>, ? extends Future<C>> fitness
	) {
		_fitness = requireNonNull(fitness);
	}

	@Override
	public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
		final ISeq<Future<C>> evaluate = population.stream()
			.filter(Phenotype::nonEvaluated)
			.map(pt -> _fitness.apply(pt.getGenotype()))
			.collect(ISeq.toISeq());

		final ISeq<Phenotype<G, C>> evaluated = population.stream()
			.filter(Phenotype::isEvaluated)
			.collect(ISeq.toISeq());

		join(evaluate);

		return evaluated.append(map(population, evaluate));
	}

	private void join(final ISeq<Future<C>> futures) {
		Exception exception = null;
		int index = 0;
		try {
			while (index < futures.size()) {
				futures.get(index).get();
				++index;
			}
		} catch (InterruptedException |
				ExecutionException |
				CancellationException e)
		{
			exception = e;
		}

		while (index < futures.size()) {
			futures.get(index).cancel(true);
			++index;
		}

		if (exception instanceof InterruptedException) {
			throw (CancellationException)
				new CancellationException(exception.getMessage())
					.initCause(exception);
		} else if (exception instanceof CancellationException) {
			throw (CancellationException)exception;
		} else if (exception != null) {
			throw new CompletionException(exception);
		}
	}

	private ISeq<Phenotype<G, C>> map(
		final Seq<Phenotype<G, C>> population,
		final Seq<Future<C>> fitnesses
	) {
		final ISeq<Phenotype<G, C>> phenotypes = population.stream()
			.filter(Phenotype::nonEvaluated)
			.collect(ISeq.toISeq());
		assert phenotypes.length() == fitnesses.length();

		final MSeq<Phenotype<G, C>> result = MSeq.ofLength(phenotypes.size());
		for (int i = 0; i < fitnesses.length(); ++i) {
			result.set(i, phenotypes.get(i).withFitness(get(fitnesses.get(i))));
		}

		return result.asISeq();
	}

	private static <T> T get(final Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException|ExecutionException e) {
			throw new AssertionError(e);
		}
	}

}

