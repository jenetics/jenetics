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

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.internal.util.IntRef;

import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ParametersCodec<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Codec<DoubleGene, Parameters>
{

	private ISeq<Proxy<Alterer<G, C>>> _alterers;



	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		final int length = _alterers.stream()
			.mapToInt(Proxy::argLength)
			.sum();

		return () -> Genotype.of(DoubleChromosome.of(0.0, 1.0, length));
	}

	@Override
	public Function<Genotype<DoubleGene>, Parameters> decoder() {
		return gt -> {
			final ISeq<Double> parameters = gt.getChromosome(0).toSeq()
				.map(DoubleGene::getAllele);

			final Iterator<double[]> altererParams = split(parameters).iterator();

			final ISeq<Alterer<G, C>> alterers = _alterers.stream()
				.flatMap(a -> a.factory()
					.apply(altererParams.next())
					.map(Stream::of)
					.orElse(Stream.empty()))
				.collect(ISeq.toISeq());

			return null;
		};
	}

	private ISeq<double[]> split(final int[] lengths, final ISeq<Double> args) {
		final IntRef start = new IntRef();
		return IntStream.of(lengths)
			.mapToObj(l -> args
				.subSeq(start.value, start.value += l).stream()
				.mapToDouble(Double::doubleValue)
				.toArray())
			.collect(ISeq.toISeq());
	}

}
