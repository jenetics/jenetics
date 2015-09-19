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

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.util.Factory;
import org.jenetics.util.Function3;
import org.jenetics.util.Function4;
import org.jenetics.util.Function5;
import org.jenetics.util.Function6;
import org.jenetics.util.ISeq;

/**
 * A problem {@code Codec} contains the information about how to encode a given
 * argument type into a {@code Genotype}. It also lets convert the encoded
 * {@code Genotype} back to the argument type. The engine creation and the
 * implementation of the fitness function can be heavily simplified by using
 * a {@code Codec} class. The example given in the {@link Engine} documentation
 * can be simplified as follows:
 *
 * <pre>{@code
 * public class RealFunction {
 *     // The conversion from the 'Genotype' to the argument type of the fitness
 *     // function is performed by the given 'Codec'. You can concentrate on the
 *     // implementation, because you are not bothered with the conversion code.
 *     private static double eval(final double x) {
 *         return cos(0.5 + sin(x)) * cos(x);
 *     }
 *
 *     public static void main(final String[] args) {
 *         final Engine<DoubleGene, Double> engine = Engine
 *              // Create an Engine.Builder with the "pure" fitness function
 *              // and the appropriate Codec.
 *             .build(RealFunction::eval, codecs.ofScalar(DoubleRange.of(0, 2*PI)))
 *             .build();
 *         ...
 *     }
 * }
 * }</pre>
 *
 * The {@code Codec} needed for the above usage example, will look like this:
 * <pre>{@code
 * final DoubleRange domain = DoubleRange.of(0, 2*PI);
 * final Codec<Double, DoubleGene> codec = Codec.of(
 *     Genotype.of(DoubleChromosome.of(domain)),
 *     gt -> gt.getChromosome().getGene().getAllele()
 * );
 * }</pre>
 *
 * @see codecs
 * @see Engine
 * @see Engine.Builder
 *
 * @param <T> the argument type of a given problem
 * @param <G> the {@code Gene} type used for encoding the argument type {@code T}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.2
 * @since 3.2
 */
public interface Codec<T, G extends Gene<?, G>> {

	/**
	 * Return the genotype factory for creating genotypes with the right
	 * encoding for the given problem. The genotype created with this factory
	 * must work together with the {@link #decoder()} function, which transforms
	 * the genotype into an object of the problem domain.
	 *
	 * <pre>{@code
	 * final Codec<SomeObject, DoubleGene> codec = ...
	 * final Genotype<DoubleGene> gt = codec.encoding().newInstance();
	 * final SomeObject arg = codec.decoder().apply(gt);
	 * }</pre>
	 *
	 * @see #decoder()
	 *
	 * @return the genotype (factory) representation of the problem domain
	 */
	public Factory<Genotype<G>> encoding();

	/**
	 * Return the <em>decoder</em> function which transforms the genotype back
	 * to the original problem domain representation.
	 *
	 * @see #encoding()
	 *
	 * @return genotype decoder
	 */
	public Function<Genotype<G>, T> decoder();


	/**
	 * Create a new {@code Codec} object with the given {@code encoding} and
	 * {@code decoder} function.
	 *
	 * @param encoding the genotype factory used for creating new
	 *        {@code Genotypes}.
	 * @param decoder decoder function, which converts a {@code Genotype} to a
	 *        value in the problem domain.
	 * @param <G> the {@code Gene} type
	 * @param <T> the fitness function argument type in the problem domain
	 * @return a new {@code Codec} object with the given parameters.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <G extends Gene<?, G>, T> Codec<T, G> of(
		final Factory<Genotype<G>> encoding,
		final Function<Genotype<G>, T> decoder
	) {
		requireNonNull(encoding);
		requireNonNull(decoder);

		return new Codec<T, G>() {
			@Override
			public Factory<Genotype<G>> encoding() {
				return encoding;
			}

			@Override
			public Function<Genotype<G>, T> decoder() {
				return decoder;
			}
		};
	}

	public static <G extends Gene<?, G>, A, B, T>
	Codec<T, G> of(
		final Codec<A, G> codec1,
		final Codec<B, G> codec2,
		final BiFunction<A, B, T> decoder
	) {
		@SuppressWarnings("unchecked")
		final Function<Object[], T> decoderAdapter = v -> decoder
			.apply((A)v[0], (B)v[1]);

		return of(
			ISeq.of(
				codec1,
				codec2
			),
			decoderAdapter
		);
	}

	public static <G extends Gene<?, G>, A, B, C, T>
	Codec<T, G> of(
		final Codec<A, G> codec1,
		final Codec<B, G> codec2,
		final Codec<C, G> codec3,
		final Function3<A, B, C, T> decoder
	) {
		@SuppressWarnings("unchecked")
		final Function<Object[], T> decoderAdapter = v -> decoder
			.apply((A)v[0], (B)v[1], (C)v[2]);

		return of(
			ISeq.of(
				codec1,
				codec2,
				codec3
			),
			decoderAdapter
		);
	}

	public static <G extends Gene<?, G>, A, B, C, D, T>
	Codec<T, G> of(
		final Codec<A, G> codec1,
		final Codec<B, G> codec2,
		final Codec<C, G> codec3,
		final Codec<D, G> codec4,
		final Function4<A, B, C, D, T> decoder
	) {
		@SuppressWarnings("unchecked")
		final Function<Object[], T> decoderAdapter = v -> decoder
			.apply((A)v[0], (B)v[1], (C)v[2], (D)v[3]);

		return of(
			ISeq.of(
				codec1,
				codec2,
				codec3,
				codec4
			),
			decoderAdapter
		);
	}

	public static <G extends Gene<?, G>, A, B, C, D, E, T>
	Codec<T, G> of(
		final Codec<A, G> codec1,
		final Codec<B, G> codec2,
		final Codec<C, G> codec3,
		final Codec<D, G> codec4,
		final Codec<E, G> codec5,
		final Function5<A, B, C, D, E, T> decoder
	) {
		@SuppressWarnings("unchecked")
		final Function<Object[], T> decoderAdapter = v -> decoder
			.apply((A)v[0], (B)v[1], (C)v[2], (D)v[3], (E)v[4]);

		return of(
			ISeq.of(
				codec1,
				codec2,
				codec3,
				codec4,
				codec5
			),
			decoderAdapter
		);
	}

	public static <G extends Gene<?, G>, A, B, C, D, E, F, T>
	Codec<T, G> of(
		final Codec<A, G> codec1,
		final Codec<B, G> codec2,
		final Codec<C, G> codec3,
		final Codec<D, G> codec4,
		final Codec<E, G> codec5,
		final Codec<F, G> codec6,
		final Function6<A, B, C, D, E, F, T> decoder
	) {
		@SuppressWarnings("unchecked")
		final Function<Object[], T> decoderAdapter = v -> decoder
			.apply((A)v[0], (B)v[1], (C)v[2], (D)v[3], (E)v[4], (F)v[5]);

		return of(
			ISeq.of(
				codec1,
				codec2,
				codec3,
				codec4,
				codec5,
				codec6
			),
			decoderAdapter
		);
	}

	public static <G extends Gene<?, G>, T>
	Codec<T, G> of(
		final ISeq<Codec<?, G>> codecs,
		final Function<Object[], T> decoder
	) {
		return new CompositeCodec<>(codecs, decoder);
	}

}
