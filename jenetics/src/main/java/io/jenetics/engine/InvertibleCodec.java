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
import java.util.function.Predicate;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.util.Factory;

/**
 * This interface extends the {@link Codec} and allows to encode an object from
 * the problem space to a corresponding {@link Genotype}, which is the
 * <em>inverse</em> functionality of the codec. The following example shows the
 * relation between <em>encoder</em> and <em>decoder</em> function must fulfill.
 *
 * <pre>{@code
 * final InvertibleCodec<int[], IntegerGene> codec =
 *     Codecs.ofVector(IntRange.of(0, 100), 6);
 * final int[] value = new int[]{3, 4, 6, 7, 8, 3};
 * final Genotype<IntegerGene> gt = codec.encode(value);
 * assert Arrays.equals(value, codec.decode(gt));
 * }</pre>
 *
 * The main usage of an invertible codec is to simplify the definition of
 * {@link Constraint} objects. Instead of working with the GA classes
 * ({@link io.jenetics.Phenotype} or {@link Genotype}), it is possible to work
 * in the <em>native</em> problem domain {@code T}.
 *
 * @see Constraint#of(InvertibleCodec, Predicate, Function)
 * @see RetryConstraint#of(InvertibleCodec, Predicate, Function)
 * @see RetryConstraint#of(InvertibleCodec, Predicate, int)
 *
 * @param <T> the argument type of given problem
 * @param <G> the {@code Gene} type used for encoding the argument type {@code T}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
public interface InvertibleCodec<T, G extends Gene<?, G>> extends Codec<T, G> {

	/**
	 * Return the <em>encoder</em> function which transforms a value from the
	 * <em>native</em> problem domain back to the genotype. This is the
	 * <em>inverse</em> of the {@link #decoder()} function. The following code
	 * snippet shows how a given value in the <em>native</em> problem domain
	 * can be converted into a {@link Genotype} and transformed back.
	 * <pre>{@code
	 * final InvertibleCodec<int[], IntegerGene> codec =
	 *     Codecs.ofVector(IntRange.of(0, 100), 6);
	 * final int[] value = new int[]{3, 4, 6, 7, 8, 3};
	 * final Genotype<IntegerGene> gt = codec.encode(value);
	 * assert Arrays.equals(value, codec.decode(gt));
	 * }</pre>
	 *
	 * @see #decoder()
	 * @see #encode(Object)
	 *
	 * @return value encoder function
	 */
	Function<T, Genotype<G>> encoder();

	/**
	 * Decodes the given {@code value}, which is an element of the <em>native</em>
	 * problem domain, into a {@link Genotype}.
	 *
	 * @param value the value of the <em>native</em> problem domain
	 * @return the genotype, which represents the given {@code value}
	 */
	default Genotype<G> encode(final T value) {
		return encoder().apply(value);
	}

	/**
	 * Create a new {@code InvertibleCodec} with the mapped result type.
	 *
	 * This method can also be used for creating non-trivial codes like split
	 * ranges, as shown in the following example, where only values between
	 * <em>[0, 2)</em> and <em>[8, 10)</em> are valid.
	 * <pre>{@code
	 *   +--+--+--+--+--+--+--+--+--+--+
	 *   |  |  |  |  |  |  |  |  |  |  |
	 *   0  1  2  3  4  5  6  7  8  9  10
	 *   |-----|xxxxxxxxxxxxxxxxx|-----|
	 *      ^  |llllllll|rrrrrrrr|  ^
	 *      |       |        |      |
	 *      +-------+        +------+
	 * }</pre>
	 *
	 * <pre>{@code
	 * final InvertibleCodec<Double, DoubleGene> codec = Codecs
	 *     .ofScalar(DoubleRange.of(0, 10))
	 *     .map(v -> {
	 *             if (v >= 2 && v < 8) {
	 *                 return v < 5 ? ((v - 2)/3)*2 : ((8 - v)/3)*2 + 8;
	 *             }
	 *             return v;
	 *         },
	 *         Function.identity());
	 * }</pre>
	 *
	 * @see Codec#map(Function)
	 *
	 * @param mapper the mapper function
	 * @param inverseMapper the inverse function of the {@code mapper}
	 * @param <B> the new argument type of the given problem
	 * @return a new {@link InvertibleCodec} with the mapped result type
	 * @throws NullPointerException if one the mapper is {@code null}.
	 */
	default <B>
	InvertibleCodec<B, G> map(
		final Function<? super T, ? extends B> mapper,
		final Function<? super B, ? extends T> inverseMapper
	) {
		requireNonNull(mapper);
		requireNonNull(inverseMapper);

		return InvertibleCodec.of(
			encoding(),
			mapper.compose(decoder()),
			encoder().compose(inverseMapper)
		);
	}

	/**
	 * Create a new invertible codec from the given parameters.
	 *
	 * @param encoding the genotype factory used for creating new
	 *        {@code Genotypes}
	 * @param decoder decoder function, which converts a {@link Genotype} to a
	 *        value in the problem domain.
	 * @param encoder encoder function, which converts a value of the problem
	 *        domain into a {@link Genotype}
	 * @param <G> the {@link Gene} type
	 * @param <T> the fitness function argument type in the problem domain
	 * @return a new {@link InvertibleCodec} object with the given parameters.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	static <T, G extends Gene<?, G>> InvertibleCodec<T, G> of(
		final Factory<Genotype<G>> encoding,
		final Function<? super Genotype<G>, ? extends T> decoder,
		final Function<? super T, Genotype<G>> encoder
	) {
		requireNonNull(encoding);
		requireNonNull(decoder);
		requireNonNull(encoder);

		return new InvertibleCodec<>() {
			@Override
			public Factory<Genotype<G>> encoding() {
				return encoding;
			}

			@Override
			@SuppressWarnings("unchecked")
			public Function<Genotype<G>, T> decoder() {
				return (Function<Genotype<G>, T>)decoder;
			}

			@Override
			@SuppressWarnings("unchecked")
			public Function<T, Genotype<G>> encoder() {
				return (Function<T, Genotype<G>>)encoder;
			}
		};
	}

}
