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
package io.jenetics.ext.grammar;

import java.util.List;
import java.util.function.Function;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.util.Factory;

import io.jenetics.ext.grammar.Cfg.Terminal;

public final class BitGeneSentenceCodec<T>
	implements Codec<List<Terminal<T>>, BitGene>
{

	private final Factory<Genotype<BitGene>> _encoding;
	private final Function<Genotype<BitGene>, List<Terminal<T>>> _decoder;

	public BitGeneSentenceCodec(
		final Cfg<? extends T> cfg,
		final int bits,
		final Function<? super SymbolIndex, ? extends Generator<T, List<Terminal<T>>>> generator
	) {
		_encoding = Genotype.of(BitChromosome.of(bits));

		_decoder = gt -> generator
			.apply(Codons.ofBitGenes(gt.chromosome()))
			.generate(cfg);
	}

	@Override
	public Factory<Genotype<BitGene>> encoding() {
		return _encoding;
	}

	@Override
	public Function<Genotype<BitGene>, List<Terminal<T>>> decoder() {
		return _decoder;
	}

}
