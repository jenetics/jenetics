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

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * Composites a list of codecs into one {@code Codec} class.
 *
 * <pre>{@code
 * final Codec<MyObject1, DoubleGene> codec1 = ...;
 * final Codec<MyObject2, DoubleGene> codec2 = ...;
 * final Codec<MyObject2, DoubleGene> codec3 = ...;
 *
 * final Codec<MyObject1, DoubleGene> codec1 = ...;
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class CompositeCodec<T, G extends Gene<?, G>>
	implements Codec<T, G>
{

	private final ISeq<Codec<?, G>> _codecs;
	private final Function<Object[], T> _decoder;

	private final int[] _lengths;
	private final Genotype<G> _encoding;

	/**
	 *
	 *
	 * @param codecs
	 * @param decoder
	 */
	CompositeCodec(
		final ISeq<Codec<?, G>> codecs,
		final Function<Object[], T> decoder
	) {
		_codecs = requireNonNull(codecs);
		_decoder = requireNonNull(decoder);

		final ISeq<Genotype<G>> genotypes = _codecs
			.map(c -> c.encoding().newInstance());

		_lengths = genotypes.stream()
			.mapToInt(Genotype::length)
			.toArray();

		_encoding = Genotype.of(
				genotypes.stream()
					.map(Genotype::toSeq)
					.flatMap(Seq::stream)
					.collect(ISeq.toISeq())
			);
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
		final ISeq<Chromosome<G>> chromosomes = genotype.toSeq();

		int start = 0;
		for (int i = 0; i < _codecs.length(); ++i) {
			final int end = start + _lengths[i];
			final Genotype<G> gt = Genotype.of(chromosomes.subSeq(start, end));

			groups[i] = _codecs.get(i).decoder().apply(gt);
			start = end;
		}

		return groups;
	}

}
