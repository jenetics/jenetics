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

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

/**
 * Composites a list of codecs into one {@code Codec} class.
 *
 * @param <G> the gene type
 * @param <T> the argument type of the compound codec
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.1
 * @since 3.3
 */
final class CompositeCodec<T, G extends Gene<?, G>> implements Codec<T, G> {

	private final ISeq<? extends Codec<?, G>> _codecs;
	private final Function<? super Object[], ? extends T> _decoder;

	private final int[] _lengths;
	private final Factory<Genotype<G>> _encoding;

	/**
	 * Combines the given {@code codecs} into one codec. This lets you divide
	 * a problem into sub problems and combine them again.
	 *
	 * @param codecs the {@code Codec} sequence of the sub-problems
	 * @param decoder the decoder which combines the argument types from the
	 *        given codecs, to the argument type of the resulting codec.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	CompositeCodec(
		final ISeq<? extends Codec<?, G>> codecs,
		final Function<? super Object[], ? extends T> decoder
	) {
		_codecs = requireNonNull(codecs);
		_decoder = requireNonNull(decoder);

		_lengths = _codecs.stream()
			.map(codec -> toGenotype(codec.encoding()))
			.mapToInt(Genotype::length)
			.toArray();

		_encoding = () -> Genotype.of(
			_codecs.stream()
				.map(codec -> codec.encoding().newInstance())
				.flatMap(Genotype::stream)
				.collect(ISeq.toISeq())
		);
	}

	private static <G extends Gene<?, G>> Genotype<G>
	toGenotype(final Factory<Genotype<G>> factory) {
		return factory instanceof Genotype<G> gt ? gt : factory.newInstance();
	}

	@Override
	public Factory<Genotype<G>> encoding() {
		return _encoding;
	}

	@Override
	public Function<Genotype<G>, T> decoder() {
		return gt -> _decoder.apply(groups(gt));
	}

	private Object[] groups(final Genotype<G> genotype) {
		final Object[] groups = new Object[_codecs.length()];
		final ISeq<Chromosome<G>> chromosomes = ISeq.of(genotype);

		int start = 0;
		for (int i = 0; i < _codecs.length(); ++i) {
			final int end = start + _lengths[i];
			final Genotype<G> gt = Genotype.of(chromosomes.subSeq(start, end));

			groups[i] = _codecs.get(i).decode(gt);
			start = end;
		}

		return groups;
	}

	@Override
	public String toString() {
		return _codecs.stream()
			.map(Objects::toString)
			.collect(Collectors.joining(",", "CompositeCodec[", "]"));
	}

}
