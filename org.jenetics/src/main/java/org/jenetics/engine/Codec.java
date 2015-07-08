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
import org.jenetics.Genotype;
import org.jenetics.util.Factory;

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
 *             .build()
 *         ...
 *     }
 * }
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
 * @version !__version__!
 * @since !__version__!
 */
public interface Codec<T, G extends Gene<?, G>> {

	/**
	 * Return the genotype factory, which represents the encoded problem domain
	 * and argument type, respectively.
	 *
	 * @return the genotype (factory) representation of the problem domain
	 */
	Factory<Genotype<G>> encoding();

	/**
	 * Return the <em>decoder</em> function which transforms the genotype back
	 * to the original problem domain representation.
	 *
	 * @return genotype decoder
	 */
	Function<Genotype<G>, T> decoder();


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
	static <G extends Gene<?, G>, T> Codec<T, G> of(
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

}
