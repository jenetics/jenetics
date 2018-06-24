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

import java.util.concurrent.Executor;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * This interface allows to define different strategies for evaluating the
 * fitness functions of a given population. <em>Normally</em>, there is no
 * need for <em>overriding</em> the default evaluation strategy, but it might
 * be necessary if you have performance problems and a <em>batched</em>
 * fitness evaluation would solve the problem.
 * <p>
 * The implementer is free to do the evaluation <em>in place</em>, or create
 * new {@link Phenotype} instance and return the newly created one. A simple
 * serial evaluator can easily implemented:
 *
 * <pre>{@code
 * final Evaluator<G, C> evaluator = population -> {
 *     population.forEach(Phenotype::evaluate);
 *     return population.asISeq();
 * };
 * }</pre>
 *
 * @implSpec
 * The size of the returned, evaluated, phenotype sequence must be exactly
 * the size of the input phenotype sequence and all phenotypes must have a
 * fitness value assigned ({@code assert population.forAll(Phenotype::isEvaluated);}).
 * It is allowed to return the input sequence, after evaluation, as well a newly
 * created one.
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.2
 */
@FunctionalInterface
public interface Evaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Evaluates the fitness values of the given {@code population}. The
	 * given {@code population} might contain already evaluated individuals.
	 * It is the responsibility of the implementer to filter out already
	 * evaluated individuals, if desired.
	 *
	 * @param population the population to evaluate
	 * @return the evaluated population. Implementers are free to return the
	 *         the input population or a newly created one.
	 */
	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population);

	/**
	 * Return a new fitness evaluator, which evaluates the fitness function of
	 * the population serially in the main thread. Might be useful for testing
	 * purpose.
	 *
	 * @since !__version__!
	 *
	 * @param function the fitness function
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new serial fitness evaluator
	 * @throws NullPointerException if the fitness {@code function} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C>
	serial(final Function<? super Genotype<G>, ? extends C> function) {
		return concurrent(function, Runnable::run);
	}

	/**
	 * Return a new fitness evaluator, which evaluates the fitness function of
	 * the population (concurrently) with the given {@code executor}. This is
	 * the default evaluator used by the evolution engine.
	 *
	 * @param function the fitness function
	 * @param executor the {@code Executor} used for evaluating the fitness
	 *        function
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (concurrent) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> concurrent(
		final Function<? super Genotype<G>, ? extends C> function,
		final Executor executor
	) {
		return new ConcurrentEvaluator<>(function, executor);
	}

}
