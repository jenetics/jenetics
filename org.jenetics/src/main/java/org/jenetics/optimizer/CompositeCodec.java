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
package org.jenetics.optimizer;

import java.util.function.Function;

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class CompositeCodec<T, G extends Gene<?, G>> implements Codec<T, G> {

	private final ISeq<Codec<?, G>> _codecs;
	private final Function<ISeq<Object>, T> _decoder;

	private final int[] _gtlengths;
	private final Genotype<G> _encoding;

	public CompositeCodec(
		final ISeq<Codec<?, G>> codecs,
		final Function<ISeq<Object>, T> decoder
	) {
		_codecs = codecs;
		_decoder = decoder;

		final ISeq<Genotype<G>> genotypes = _codecs.stream()
			.map(c -> c.encoding().newInstance())
			.collect(ISeq.toISeq());

		_gtlengths = genotypes.stream()
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

	private ISeq<Object> groups(final Genotype<G> genotype) {
		final MSeq<Object> groups = MSeq.ofLength(_codecs.length());
		final ISeq<Chromosome<G>> chromosomes = genotype.toSeq();

		int start = 0;
		for (int i = 0; i < _codecs.length(); ++i) {
			final int end = start + _gtlengths[i];

			final Genotype<G> gt = Genotype.of(chromosomes.subSeq(start, end));
			groups.set(i, _codecs.get(i).decoder().apply(gt));

			start = end;
		}

		return groups.toISeq();
	}

}
