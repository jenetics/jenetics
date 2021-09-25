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

import java.util.function.BiFunction;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

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
 *             .build(RealFunction::eval, Codecs.ofScalar(DoubleRange.of(0, 2*PI)))
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
 *     gt -> gt.chromosome().gene().allele()
 * );
 * }</pre>
 *
 * Calling the {@link Codec#of(Factory, Function)} method is the usual way for
 * creating new {@code Codec} instances.
 *
 * @see Codecs
 * @see Engine
 * @see Engine.Builder
 *
 * @param <T> the argument type of given problem
 * @param <G> the {@code Gene} type used for encoding the argument type {@code T}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.6
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
	Factory<Genotype<G>> encoding();

	/**
	 * Return the <em>decoder</em> function which transforms the genotype back
	 * to the original problem domain representation.
	 *
	 * @see #encoding()
	 *
	 * @return genotype decoder
	 */
	Function<Genotype<G>, T> decoder();

	/**
	 * Converts the given {@link Genotype} to the target type {@link T}. This is
	 * a shortcut for
	 * <pre>{@code
	 * final Codec<SomeObject, DoubleGene> codec = ...
	 * final Genotype<DoubleGene> gt = codec.encoding().newInstance();
	 *
	 * final SomeObject arg = codec.decoder().apply(gt);
	 * }</pre>
	 *
	 * @since 3.6
	 *
	 * @param genotype the genotype to be converted
	 * @return the converted genotype
	 * @throws NullPointerException if the given {@code genotype} is {@code null}
	 */
	default T decode(final Genotype<G> genotype) {
		requireNonNull(genotype);
		return decoder().apply(genotype);
	}

	/**
	 * Create a new {@code Codec} with the mapped result type. The following
	 * example creates a double codec who's values are not uniformly distributed
	 * between {@code [0..1)}. Instead the values now follow an exponential
	 * function.
	 *
	 * <pre>{@code
	 *  final Codec<Double, DoubleGene> c = Codecs.ofScalar(DoubleRange.of(0, 1))
	 *      .map(Math::exp);
	 * }</pre>
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
	 * final Codec<Double, DoubleGene> codec = Codecs
	 *     .ofScalar(DoubleRange.of(0, 10))
	 *     .map(v -> {
	 *             if (v >= 2 && v < 8) {
	 *                 return v < 5 ? ((v - 2)/3)*2 : ((8 - v)/3)*2 + 8;
	 *             }
	 *             return v;
	 *         });
	 * }</pre>
	 *
	 * @since 4.0
	 *
	 * @see InvertibleCodec#map(Function, Function)
	 *
	 * @param mapper the mapper function
	 * @param <B> the new argument type of the given problem
	 * @return a new {@code Codec} with the mapped result type
	 * @throws NullPointerException if the mapper is {@code null}.
	 */
	default <B> Codec<B, G> map(final Function<? super T, ? extends B> mapper) {
		requireNonNull(mapper);

		return Codec.of(
			encoding(),
			mapper.compose(decoder())
		);
	}

	/**
	 * Converts this codec into an <em>invertible</em> codec, by using the given
	 * {@code encoder} (inversion) function.
	 *
	 * @param encoder the (inverse) encoder function
	 * @return a new invertible codec
	 * @throws NullPointerException if the given {@code encoder} is {@code null}
	 */
	default InvertibleCodec<T, G>
	toInvertibleCodec(final Function<? super T, Genotype<G>> encoder) {
		return InvertibleCodec.of(
			encoding(),
			decoder(),
			encoder
		);
	}

	/**
	 * Create a new {@code Codec} object with the given {@code encoding} and
	 * {@code decoder} function.
	 *
	 * @param encoding the genotype factory used for creating new
	 *        {@code Genotypes}
	 * @param decoder decoder function, which converts a {@code Genotype} to a
	 *        value in the problem domain
	 * @param <G> the {@code Gene} type
	 * @param <T> the fitness function argument type in the problem domain
	 * @return a new {@code Codec} object with the given parameters
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	static <T, G extends Gene<?, G>> Codec<T, G> of(
		final Factory<Genotype<G>> encoding,
		final Function<? super Genotype<G>, ? extends T> decoder
	) {
		requireNonNull(encoding);
		requireNonNull(decoder);

		return new Codec<>() {
			@Override
			public Factory<Genotype<G>> encoding() {
				return encoding;
			}

			@Override
			@SuppressWarnings("unchecked")
			public Function<Genotype<G>, T> decoder() {
				return (Function<Genotype<G>, T>)decoder;
			}
		};
	}


	/**
	 * Converts two given {@code Codec} instances into one. This lets you divide
	 * a problem into sub problems and combine them again.
	 * <p>
	 * The following example shows how to combine two codecs, which converts a
	 * {@code LongGene} to a {@code LocalDate}, to a codec which combines the
	 * two {@code LocalDate} object (these are the argument types of the
	 * component codecs) to a {@code Duration}.
	 *
	 * <pre>{@code
	 * final Codec<LocalDate, LongGene> dateCodec1 = Codec.of(
	 *     Genotype.of(LongChromosome.of(0, 10_000)),
	 *     gt -> LocalDate.ofEpochDay(gt.gene().longValue())
	 * );
	 *
	 * final Codec<LocalDate, LongGene> dateCodec2 = Codec.of(
	 *     Genotype.of(LongChromosome.of(1_000_000, 10_000_000)),
	 *     gt -> LocalDate.ofEpochDay(gt.gene().longValue())
	 * );
	 *
	 * final Codec<Duration, LongGene> durationCodec = Codec.of(
	 *     dateCodec1,
	 *     dateCodec2,
	 *     (d1, d2) -> Duration.ofDays(d2.toEpochDay() - d1.toEpochDay())
	 * );
	 *
	 * final Engine<LongGene, Long> engine = Engine
	 *     .builder(Duration::toMillis, durationCodec)
	 *     .build();
	 *
	 * final Phenotype<LongGene, Long> pt = engine.stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestPhenotype());
	 * System.out.println(pt);
	 *
	 * final Duration duration = durationCodec.decoder()
	 *     .apply(pt.genotype());
	 * System.out.println(duration);
	 * }</pre>
	 *
	 * @since 3.3
	 *
	 * @param <G> the gene type
	 * @param <A> the argument type of the first codec
	 * @param <B> the argument type of the second codec
	 * @param <T> the argument type of the compound codec
	 * @param codec1 the first codec
	 * @param codec2 the second codec
	 * @param decoder the decoder which combines the two argument types from the
	 *        given codecs, to the argument type of the resulting codec.
	 * @return a new codec which combines the given {@code codec1} and
	 *        {@code codec2}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static <A, B, T, G extends Gene<?, G>> Codec<T, G> of(
		final Codec<A, G> codec1,
		final Codec<B, G> codec2,
		final BiFunction<A, B, T> decoder
	) {
		@SuppressWarnings("unchecked")
		final Function<Object[], T> decoderAdapter =
			v -> decoder.apply((A)v[0], (B)v[1]);

		return of(
			ISeq.of(codec1, codec2),
			decoderAdapter
		);
	}

	/**
	 * Combines the given {@code codecs} into one codec. This lets you divide
	 * a problem into sub problems and combine them again.
	 * <p>
	 * The following example combines more than two sub-codecs into one.
	 * <pre>{@code
	 * final Codec<LocalDate, LongGene> dateCodec = Codec.of(
	 *     Genotype.of(LongChromosome.of(0, 10_000)),
	 *     gt -> LocalDate.ofEpochDay(gt.getGene().longValue())
	 * );
	 *
	 * final Codec<Duration, LongGene> durationCodec = Codec.of(
	 *     ISeq.of(dateCodec, dateCodec, dateCodec),
	 *     dates -> {
	 *         final LocalDate ld1 = (LocalDate)dates[0];
	 *         final LocalDate ld2 = (LocalDate)dates[1];
	 *         final LocalDate ld3 = (LocalDate)dates[2];
	 *
	 *         return Duration.ofDays(
	 *             ld1.toEpochDay() + ld2.toEpochDay() - ld3.toEpochDay()
	 *         );
	 *     }
	 * );
	 *
	 * final Engine<LongGene, Long> engine = Engine
	 *     .builder(Duration::toMillis, durationCodec)
	 *     .build();
	 *
	 * final Phenotype<LongGene, Long> pt = engine.stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestPhenotype());
	 * System.out.println(pt);
	 *
	 * final Duration duration = durationCodec.decoder()
	 *     .apply(pt.genotype());
	 * System.out.println(duration);
	 * }</pre>
	 *
	 * @since 3.3
	 *
	 * @param <G> the gene type
	 * @param <T> the argument type of the compound codec
	 * @param codecs the {@code Codec} sequence of the sub-problems
	 * @param decoder the decoder which combines the argument types from the
	 *        given codecs, to the argument type of the resulting codec.
	 * @return a new codec which combines the given {@code codecs}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code codecs} sequence is
	 *         empty
	 */
	static <T, G extends Gene<?, G>> Codec<T, G> of(
		final ISeq<? extends Codec<?, G>> codecs,
		final Function<? super Object[], ? extends T> decoder
	) {
		if (codecs.isEmpty()) {
			throw new IllegalArgumentException(
				"Codecs sequence must not be empty."
			);
		}
		return codecs.size() == 1
			? Codec.of(
				codecs.get(0).encoding(),
				gt -> {
					final Object value = codecs.get(0).decoder().apply(gt);
					return decoder.apply(new Object[]{value});
				})
			: new CompositeCodec<>(codecs, decoder);
	}

}
