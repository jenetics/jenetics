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
import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Selector;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ParametersCodec<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Codec<DoubleGene, Parameters<G, C>>
{

	private final ISeq<Proxy<Alterer<G, C>>> _alterers;
	private final ISeq<Proxy<Selector<G, C>>> _offspringSelectors;
	private final ISeq<Proxy<Selector<G, C>>> _survivorsSelectors;
	private final int _minPopulationSize;
	private final int _maxPopulationSize;
	private final long _minMaxPhenotypeAge;
	private final long _maxMaxPhenotypeAge;

	public ParametersCodec(
		final ISeq<Proxy<Alterer<G, C>>> alterers,
		final ISeq<Proxy<Selector<G, C>>> offspringSelectors,
		final ISeq<Proxy<Selector<G, C>>> survivorsSelectors,
		final int minPopulationSize,
		final int maxPopulationSize,
		final long minMaxPhenotypeAge,
		final long maxMaxPhenotypeAge
	) {
		_alterers = alterers;
		_offspringSelectors = offspringSelectors;
		_survivorsSelectors = survivorsSelectors;
		_minPopulationSize = minPopulationSize;
		_maxPopulationSize = maxPopulationSize;
		_minMaxPhenotypeAge = minMaxPhenotypeAge;
		_maxMaxPhenotypeAge = maxMaxPhenotypeAge;
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return () -> Genotype.of(
			// Alterers
			DoubleChromosome.of(0.0, 1.0,
				_alterers.stream().mapToInt(Proxy::argsLength).sum()
			),
			// Offspring selectors
			DoubleChromosome.of(0.0, 1.0,
				_offspringSelectors.stream().mapToInt(Proxy::argsLength).sum()
			),
			// Survivors selectors
			DoubleChromosome.of(0.0, 1.0,
				_survivorsSelectors.stream().mapToInt(Proxy::argsLength).sum()
			),
			// Offspring fraction
			DoubleChromosome.of(0.0, 1.0),
			// Population size
			DoubleChromosome.of(_minPopulationSize, _maxPopulationSize),
			// Phenotype age
			DoubleChromosome.of(_minMaxPhenotypeAge, _maxMaxPhenotypeAge)
		);
	}

	@Override
	public Function<Genotype<DoubleGene>, Parameters<G, C>> decoder() {
		return gt -> {
			final ISeq<Alterer<G, C>> alterers =
				instances(_alterers, gt.getChromosome(0));

			final ISeq<Selector<G, C>> offspringSelectors =
				instances(_offspringSelectors, gt.getChromosome(1));

			final ISeq<Selector<G, C>> survivorsSelectors =
				instances(_offspringSelectors, gt.getChromosome(2));

			final double offspringFraction = gt.getChromosome(3)
				.getGene().doubleValue();

			final int populationSize = gt.getChromosome(4)
				.getGene().intValue();

			final long maxPhenotypeAge = gt.getChromosome(5)
				.getGene().longValue();

			/*
			return Parameters.<G, C>of(
				alterers,
				offspringSelectors,
				survivorsSelectors,
				offspringFraction,
				populationSize,
				maxPhenotypeAge
			);
			*/

			return null;
		};
	}

	private static <T> ISeq<T> instances(
		final Seq<Proxy<T>> proxies,
		final Chromosome<DoubleGene> chromosome
	) {
		final ISeq<Double> arguments = chromosome.toSeq()
			.map(DoubleGene::getAllele);

		final int[] lengths = proxies.stream()
			.mapToInt(Proxy::argsLength)
			.toArray();

		final Iterator<double[]> args = split(lengths, arguments).iterator();
		return proxies.stream()
			.flatMap(a -> a.factory()
				.apply(args.next())
				.map(Stream::of)
				.orElse(Stream.empty()))
			.collect(ISeq.toISeq());
	}

	private static ISeq<double[]> split(
		final int[] lengths,
		final Seq<Double> args
	) {
		final IntRef start = new IntRef();
		return IntStream.of(lengths)
			.mapToObj(l -> args
				.subSeq(start.value, start.value += l).stream()
				.mapToDouble(Double::doubleValue)
				.toArray())
			.collect(ISeq.toISeq());
	}

}
