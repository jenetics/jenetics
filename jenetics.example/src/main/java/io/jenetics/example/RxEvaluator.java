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
package io.jenetics.example;

import static java.util.Objects.requireNonNull;

import io.reactivex.Observable;

import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Evaluator;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Example of an {@code Engine.Evaluator} where the fitness function returns
 * an Observable of the fitness value instead the value itself.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.3
 */
public final class RxEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evaluator<G, C>
{

	private final Function<? super Genotype<G>, Observable<C>> _fitness;

	public RxEvaluator(final Function<? super Genotype<G>, Observable<C>> fitness) {
		_fitness = requireNonNull(fitness);
	}

	@Override
	public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
		final Stream<Observable<Phenotype<G, C>>> result = Stream.concat(
			population.stream()
				.filter(Phenotype::isEvaluated)
				.map(Observable::just),
			population.stream()
				.filter(Phenotype::nonEvaluated)
				.map(pt -> _fitness.apply(pt.getGenotype())
								.map(pt::withFitness))
		);

		return result
			.collect(ISeq.toISeq())
			.map(Observable::blockingSingle);
	}

	public static void main(final String[] args) {
		final Factory<Genotype<DoubleGene>> gtf =
			Genotype.of(DoubleChromosome.of(0, 1));

		final Engine<DoubleGene, Double> engine = new Engine.Builder<>
			(new RxEvaluator<DoubleGene, Double>(RxEvaluator::fitness), gtf)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(100)
			.collect(EvolutionResult.toBestEvolutionResult());

		System.out.println(result.getBestPhenotype());
	}

	private static Observable<Double> fitness(final Genotype<DoubleGene> gt) {
		return Observable.just(gt.getGene().doubleValue());
	}

}
