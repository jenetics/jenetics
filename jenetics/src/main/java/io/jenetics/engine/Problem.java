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

/**
 * This interface describes a <i>problem</i> which can be solved by the GA
 * evolution {@code Engine}. It connects the actual {@link #fitness()} function
 * and the needed {@link #codec()}.
 *
 * <pre>{@code
 * final Problem<ISeq<BitGene>, BitGene, Integer> counting = Problem.of(
 *     // Native fitness function
 *     genes -> (int)genes.stream()
 *         .filter(BitGene::getBit)
 *         .count(),
 *     // Problem encoding
 *     Codec.of(
 *         Genotype.of(BitChromosome.of(100)),
 *         gt -> gt.getChromosome().toSeq()
 *     )
 * );
 * }</pre>
 *
 * The example above shows the Ones-Counting problem definition.
 *
 * @see Codec
 * @see Engine
 *
 * @param <T> the (<i>native</i>) argument type of the problem fitness function
 * @param <G> the gene type the evolution engine is working with
 * @param <C> the result type of the fitness function
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.2
 * @since 3.4
 */
public interface Problem<
	T,
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

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
	 * Converts the given {@link Genotype} to the target type {@link T}. This is
	 * a shortcut for
	 * <pre>{@code
	 * final Problem<SomeObject, DoubleGene, Double> problem = ...
	 * final Genotype<DoubleGene> gt = problem.codec().encoding().newInstance();
	 *
	 * final SomeObject arg = problem.decode(gt);
	 * }</pre>
	 *
	 * @since 4.2
	 *
	 * @see Codec#decode(Genotype)
	 *
	 * @param genotype the genotype to be converted
	 * @return the converted genotype
	 * @throws NullPointerException if the given {@code genotype} is {@code null}
	 */
	public default T decode(final Genotype<G> genotype) {
		return codec().decode(genotype);
	}

	/**
	 * Returns the fitness value for the given argument.
	 *
	 * @since 4.1
	 *
	 * @param arg the argument of the fitness function
	 * @return the fitness value
	 */
	public default C fitness(final T arg) {
		return fitness().apply(arg);
	}

	/**
	 * Returns the fitness value for the given argument.
	 *
	 * @since 4.1
	 *
	 * @param genotype the argument of the fitness function
	 * @return the fitness value
	 */
	public default C fitness(final Genotype<G> genotype) {
		return fitness(codec().decode(genotype));
	}

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

