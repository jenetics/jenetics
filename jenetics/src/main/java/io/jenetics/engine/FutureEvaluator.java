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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Example of an {@code Engine.Evaluator} where the fitness function returns
 * a {@link Future} of the fitness value instead the value itself.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
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
	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population) {
		final Stream<Future<Phenotype<G, C>>> result =
			Stream.concat(
				population.stream()
					.filter(Phenotype::isEvaluated)
					.map(CompletableFuture::completedFuture),
				population.stream()
					.filter(Phenotype::nonEvaluated)
					.map(pt -> new MappedFuture<>(
						_fitness.apply(pt.getGenotype()), pt::withFitness))
			);

		return result
			.collect(ISeq.toISeq())
			.map(FutureEvaluator::join);
	}

	private static <T> T join(final Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException e) {
			throw (CancellationException)new CancellationException(e.getMessage())
				.initCause(e);
		} catch (ExecutionException e) {
			throw new CompletionException(e);
		}
	}


	private static final class MappedFuture<A, B> implements Future<B> {

		private final Future<A> _adoptee;
		private final Function<A, B> _mapper;

		MappedFuture(final Future<A> adoptee, final Function<A, B> mapper) {
			_adoptee = adoptee;
			_mapper = mapper;
		}

		@Override
		public boolean cancel(final boolean mayInterruptIfRunning) {
			return _adoptee.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return _adoptee.isCancelled();
		}

		@Override
		public boolean isDone() {
			return _adoptee.isDone();
		}

		@Override
		public B get() throws InterruptedException, ExecutionException {
			return _mapper.apply(_adoptee.get());
		}

		@Override
		public B get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException
		{
			return _mapper.apply(_adoptee.get(timeout, unit));
		}
	}
}

