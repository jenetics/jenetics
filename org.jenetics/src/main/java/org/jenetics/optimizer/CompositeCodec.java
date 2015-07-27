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

import static java.lang.String.format;

import java.util.Arrays;
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

	private final ISeq<ISeq<Chromosome<G>>> _chromosomes;
	private final int[] _indexes;
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

		_indexes = genotypes.stream()
			.mapToInt(Genotype::length)
			.toArray();
		System.out.println(Arrays.toString(_indexes));

		/*
		for (int i = 1; i < _indexes.length; ++i) {
			_indexes[i] += _indexes[i - 1];
		}
		System.out.println(Arrays.toString(_indexes));
		 */

		_chromosomes = genotypes.stream()
			.map(Genotype::toSeq)
			.collect(ISeq.toISeq());

		/*
		_indexes = new int[_chromosomes.size()];
		_indexes[0] = _chromosomes.get(0).size();
		for (int i = 1; i < _indexes.length; ++i) {
			_indexes[i] = _indexes[i - 1] + _chromosomes.get(i).size();
		}
		*/

		_encoding = Genotype.of(
			_chromosomes.stream().flatMap(Seq::stream).collect(ISeq.toISeq())
		);
		System.out.println(_encoding + ": " +_encoding.length());
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
		//System.out.println("GT: " + genotype);
		final MSeq<Object> groups = MSeq.ofLength(_codecs.length());

		final ISeq<Chromosome<G>> chromosomes = genotype.toSeq();
		//System.out.println(chromosomes.subSeq(0, 1));

		int start = 0;
		for (int i = 0; i < _codecs.length(); ++i) {
			final int end = start + _indexes[i];

			System.out.println(format("i=%d, start=%d, end=%d, length=%d", i, start, end, _indexes[i]));
			final Genotype<G> gt = Genotype
				.of(
					chromosomes.subSeq(start, end)
				);

			groups.set(i, _codecs.get(i).decoder().apply(gt));

			start = end;
		}

		return groups.toISeq();
	}

}
