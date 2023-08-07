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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.internal.concurrent.CompletableFutureEvaluator;
import io.jenetics.internal.concurrent.FutureEvaluator;

/**
 * This class contains factory methods for creating commonly usable
 * {@link Evaluator} implementations.
 *
 * @see Evaluator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 5.0
 */
public final class Evaluators {
	private Evaluators() {}

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @see #completable(Function)
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

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @param fitness the asynchronous fitness function, working on the
	 *        <em>native</em> fitness domain
	 * @param decoder the decoder function for the fitness domain
	 * @param <T> the <em>native</em> fitness domain type
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (async) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> async(
		final Function<? super T, ? extends Future<C>> fitness,
		final Function<? super Genotype<G>, ? extends T> decoder
	) {
		return async(fitness.compose(decoder));
	}

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @param fitness the asynchronous fitness function, working on the
	 *        <em>native</em> fitness domain
	 * @param codec the codec used for transforming the fitness domain
	 * @param <T> the <em>native</em> fitness domain type
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (async) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> async(
		final Function<? super T, ? extends Future<C>> fitness,
		final Codec<T, G> codec
	) {
		return async(fitness, codec.decoder());
	}

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @see #async(Function)
	 *
	 * @param fitness the asynchronous fitness function
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (asynchronous) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C>
	completable(
		final Function<
			? super Genotype<G>,
			? extends CompletableFuture<C>> fitness
	) {
		return new CompletableFutureEvaluator<>(fitness);
	}

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @param fitness the asynchronous fitness function, working on the
	 *        <em>native</em> fitness domain
	 * @param decoder the decoder function for the fitness domain
	 * @param <T> the <em>native</em> fitness domain type
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (async) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> completable(
		final Function<? super T, ? extends CompletableFuture<C>> fitness,
		final Function<? super Genotype<G>, ? extends T> decoder
	) {
		return completable(fitness.compose(decoder));
	}

	/**
	 * Return a new fitness evaluator, which evaluates <em>asynchronous</em>
	 * fitness functions.
	 *
	 * @param fitness the asynchronous fitness function, working on the
	 *        <em>native</em> fitness domain
	 * @param codec the codec used for transforming the fitness domain
	 * @param <T> the <em>native</em> fitness domain type
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new (async) fitness evaluator
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Evaluator<G, C> completable(
		final Function<? super T, ? extends CompletableFuture<C>> fitness,
		final Codec<T, G> codec
	) {
		return completable(fitness, codec.decoder());
	}

}
