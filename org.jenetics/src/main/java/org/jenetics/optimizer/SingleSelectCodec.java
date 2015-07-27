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
import java.util.stream.Stream;

import org.jenetics.internal.util.require;

import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class SingleSelectCodec<T> implements Codec<T, DoubleGene> {

	private final ISeq<Codec<T, DoubleGene>> _codecs;

	private final ISeq<ISeq<Chromosome<DoubleGene>>> _chromosomes;
	private final int[] _indexes;
	private final Genotype<DoubleGene> _encoding;

	public SingleSelectCodec(final ISeq<Codec<T, DoubleGene>> codecs) {
		_codecs = codecs;

		_chromosomes = _codecs.stream()
			.map(c -> c.encoding().newInstance().toSeq())
			.collect(ISeq.toISeq());

		_indexes = new int[_chromosomes.size()];
		_indexes[0] = _chromosomes.get(0).size();
		for (int i = 1; i < _indexes.length; ++i) {
			_indexes[i] = _indexes[i - 1] + _chromosomes.get(i).size();
		}

		_encoding = Genotype.of(
			Stream.concat(
				Stream.of(DoubleChromosome.of(0.0, 1.0)),
				_chromosomes.stream().flatMap(Seq::stream)
			).collect(ISeq.toISeq())
		);
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _encoding;
	}

	@Override
	public Function<Genotype<DoubleGene>, T> decoder() {
		return gt -> {
			final int index = toIndex(gt.toSeq().get(0).getGene().getAllele());

			final int start = _indexes[index] + 1;
			final int length = _chromosomes.get(index).size();
			final ISeq<Chromosome<DoubleGene>> chromosomes = gt.toSeq()
				.subSeq(start, start + length);

			return _codecs.get(index).decoder().apply(Genotype.of(chromosomes));
		};
	}

	private int toIndex(final double p) {
		require.probability(p);
		final double prob = Double.compare(p, 1.0) == 0 ? Math.nextDown(p) : p;
		return (int)(prob*_codecs.length());
	}
}
