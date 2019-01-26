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
import java.util.concurrent.Future;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;

/**
 * This class contains factory methods for creating commonly usable
 * {@link Evaluator} implementations.
 *
 * @see Evaluator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Evaluators {
	private Evaluators() {}

	/**
	 * Return a new fitness evaluator, which evaluates the fitness function of
	 * the population serially in the main thread. Might be useful for testing
	 * purpose.
	 *
	 * @since !__version__!
	 *
	 * @param fitness the fitness function
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new serial fitness evaluator
	 * @throws NullPointerException if the fitness {@code function} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C>
	serial(final Function<? super Genotype<G>, ? extends C> fitness) {
		return concurrent(fitness, Runnable::run);
	}

	/**
	 * Return a new fitness evaluator, which evaluates the fitness function of
	 * the population (concurrently) with the given {@code executor}. This is
	 * the default evaluator used by the evolution engine.
	 *
	 * @since !__version__!
	 *
	 * @param fitness the fitness function
	 * @param executor the {@code Executor} used for evaluating the fitness
	 *        function
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (concurrent) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> concurrent(
		final Function<? super Genotype<G>, ? extends C> fitness,
		final Executor executor
	) {
		return new ConcurrentEvaluator<>(fitness, executor);
	}

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @since !__version__!
	 *
	 * @param fitness the asynchronous fitness function
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (asynchronous) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C>
	async(final Function<? super Genotype<G>, ? extends Future<C>> fitness) {
		return new FutureEvaluator<>(fitness);
	}

}
