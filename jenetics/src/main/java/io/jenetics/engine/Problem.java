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

import java.util.Optional;
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
 *         .filter(BitGene::bit)
 *         .count(),
 *     // Problem encoding
 *     Codec.of(
 *         Genotype.of(BitChromosome.of(100)),
 *         gt -> ISeq.of(gt.chromosome())
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
 * @version 6.1
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
	Function<T, C> fitness();

	/**
	 * Return the codec, which translates the types of the problem domain into
	 * types, which can be understood by the evolution {@code Engine}.
	 *
	 * @return the engine codec
	 */
	Codec<T, G> codec();

	/**
	 * Return the constraint, associated with {@code this} problem, if available.
	 *
	 * @since 6.1
	 *
	 * @return the constraint, associated with {@code this} problem
	 */
	default Optional<Constraint<G, C>> constraint() {
		return Optional.empty();
	}

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
	default T decode(final Genotype<G> genotype) {
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
	default C fitness(final T arg) {
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
	default C fitness(final Genotype<G> genotype) {
		return fitness(codec().decode(genotype));
	}

	/**
	 * Return a new optimization <i>problem</i> with the given parameters. The
	 * given {@code constraint} is applied to the {@link Engine}, via
	 * {@link Engine.Builder#constraint(Constraint)}, and the {@link Codec}, via
	 * {@link Constraint#constrain(Codec)}.
	 * <p>
	 * <b>Note</b><br>
	 *     When creating a new {@code Problem} instance with this factory method,
	 *     there is no need for additionally <em>constraining</em> the given
	 *     {@code codec} with {@link Constraint#constrain(Codec)}.
	 *
	 * @since 6.1
	 *
	 * @see Engine.Builder#constraint(Constraint)
	 * @see Constraint#constrain(Codec)
	 *
	 * @param fitness the problem fitness function
	 * @param codec the evolution engine codec
	 * @param constraint the problem constraint, may be {@code null}
	 * @param <T> the (<i>native</i>) argument type of the problem fitness function
	 * @param <G> the gene type the evolution engine is working with
	 * @param <C> the result type of the fitness function
	 * @return a new problem object from the given parameters
	 * @throws NullPointerException if the {@code fitness} or {@code codec} is
	 *         {@code null}
	 */
	static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Problem<T, G, C> of(
		final Function<T, C> fitness,
		final Codec<T, G> codec,
		final Constraint<G, C> constraint
	) {
		requireNonNull(fitness);
		requireNonNull(codec);

		final var constrainedCodec = wrap(constraint, codec);

		return new Problem<>() {
			@Override
			public Codec<T, G> codec() {
				return constrainedCodec;
			}
			@Override
			public Function<T, C> fitness() {
				return fitness;
			}
			@Override
			public Optional<Constraint<G, C>> constraint() {
				return Optional.ofNullable(constraint);
			}
		};
	}

	private static  <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<T, G> wrap(final Constraint<G, C> constraint, final Codec<T, G> codec) {
		Codec<T, G> result = codec;
		if (constraint != null) {
			result = codec instanceof InvertibleCodec<T, G> ic
				? constraint.constrain(ic)
				: constraint.constrain(codec);
		}

		return result;
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
	static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Problem<T, G, C> of(
		final Function<T, C> fitness,
		final Codec<T, G> codec
	) {
		return of(fitness, codec, null);
	}

}

