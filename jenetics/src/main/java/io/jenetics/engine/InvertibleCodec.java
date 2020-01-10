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
import io.jenetics.util.Factory;

/**
 * This interface extends the {@link Codec} and allows to encode an object from
 * the problem space to a corresponding {@link Genotype}, which is the
 * <em>inverse</em> functionality of the codec.
 *
 * @param <T> the argument type of a given problem
 * @param <G> the {@code Gene} type used for encoding the argument type {@code T}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface InvertibleCodec<T, G extends Gene<?, G>> extends Codec<T, G> {

	/**
	 * Return the <em>encoder</em> function which transforms a value from the
	 * <em>native</em> problem domain back to the genotype. This is the
	 * <em>inverse</em> of the {@link #decoder()} function.
	 *
	 * <pre>{@code
	 * final Genotype<DoubleGene> gt =
	 * }</pre>
	 *
	 * @see #encoding()
	 *
	 * @return genotype decoder
	 */
	public Function<T, Genotype<G>> encoder();

	public default Genotype<G> encode(final T value) {
		return encoder().apply(value);
	}

	public static <T, G extends Gene<?, G>> InvertibleCodec<T, G> of(
		final Factory<Genotype<G>> encoding,
		final Function<Genotype<G>, T> decoder,
		final Function<T, Genotype<G>> encoder
	) {
		requireNonNull(encoding);
		requireNonNull(decoder);
		requireNonNull(encoder);

		return new InvertibleCodec<T, G>() {
			@Override
			public Function<T, Genotype<G>> encoder() {
				return encoder;
			}

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
