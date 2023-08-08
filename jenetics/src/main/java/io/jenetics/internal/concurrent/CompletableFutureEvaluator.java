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

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Evaluator;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 5.0
 */
public final class CompletableFutureEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>
{

	private final Function<? super Genotype<G>, ? extends CompletableFuture<C>> _fitness;

	public CompletableFutureEvaluator(
		final Function<? super Genotype<G>, ? extends CompletableFuture<C>> fitness
	) {
		_fitness = requireNonNull(fitness);
	}

	@Override
	public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
		@SuppressWarnings("unchecked")
		final CompletableFuture<C>[] evaluate =
			(CompletableFuture<C>[])population.stream()
				.filter(Phenotype::nonEvaluated)
				.map(pt -> _fitness.apply(pt.genotype()))
				.toArray(CompletableFuture[]::new);

		final ISeq<Phenotype<G, C>> evaluated = population.stream()
			.filter(Phenotype::isEvaluated)
			.collect(ISeq.toISeq());

		CompletableFuture.allOf(evaluate).join();

		return evaluated.append(map(population, evaluate));
	}

	private ISeq<Phenotype<G, C>> map(
		final Seq<Phenotype<G, C>> population,
		final CompletableFuture<C>[] fitnesses
	) {
		final ISeq<Phenotype<G, C>> phenotypes = population.stream()
			.filter(Phenotype::nonEvaluated)
			.collect(ISeq.toISeq());
		assert phenotypes.length() == fitnesses.length;

		final MSeq<Phenotype<G, C>> result = MSeq.ofLength(phenotypes.size());
		for (int i = 0; i < fitnesses.length; ++i) {
			result.set(i, phenotypes.get(i).withFitness(fitnesses[i].join()));
		}

		return result.asISeq();
	}

}
