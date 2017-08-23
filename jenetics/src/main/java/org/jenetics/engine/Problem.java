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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.jenetics.Gene;

/**
 * This interface describes a <i>problem</i> which can be solved by the GA
 * evolution {@code Engine}. It connects the actual {@link #fitness()} function
 * and the needed {@link #codec()}.
 *
 * @see Codec
 * @see Engine
 *
 * @param <T> the (<i>native</i>) argument type of the problem fitness function
 * @param <G> the gene type the evolution engine is working with
 * @param <C> the result type of the fitness function
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public interface Problem<
	T,
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	/**
	 * Return the fitness function of the <i>problem</i> in the <i>native</i>
	 * problem domain.
	 *
	 * @return the fitness function
	 */
	public Function<T, C> fitness();

	/**
	 * Return the codec, which translates the types of the problem domain into
	 * types, which can be understand by the evolution {@code Engine}.
	 *
	 * @return the engine codec
	 */
	public Codec<T, G> codec();

	/**
	 * Return a new optimization <i>problem</i> with the given parameters.
	 *
	 * @param fitness the problem fitness function
	 * @param codec the evolution engine codec
	 * @param <T> the (<i>native</i>) argument type of the problem fitness function
	 * @param <G> the gene type the evolution engine is working with
	 * @param <C> the result type of the fitness function
	 * @return a new problem object from the given parameters
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Problem<T, G, C> of(
		final Function<T, C> fitness,
		final Codec<T, G> codec
	) {
		requireNonNull(fitness);
		requireNonNull(codec);

		return new Problem<T, G, C>() {
			@Override
			public Codec<T, G> codec() {
				return codec;
			}

			@Override
			public Function<T, C> fitness() {
				return fitness;
			}
		};
	}

}

