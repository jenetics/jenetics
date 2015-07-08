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
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Selector;
import org.jenetics.engine.Codec;
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
	implements Codec<Parameters<G, C>, DoubleGene>
{

	private final ISeq<Proxy<Alterer<G, C>>> _alterers;
	private final ISeq<Proxy<Selector<G, C>>> _offspringSelectors;
	private final ISeq<Proxy<Selector<G, C>>> _survivorsSelectors;
	private final IntRange _populationSize;
	private final IntRange _maxPhenotypeAge;

	private final Factory<Genotype<DoubleGene>> _encoding;

	public ParametersCodec(
		final ISeq<Proxy<Alterer<G, C>>> alterers,
		final ISeq<Proxy<Selector<G, C>>> offspringSelectors,
		final ISeq<Proxy<Selector<G, C>>> survivorsSelectors,
		final IntRange populationSize,
		final IntRange maxPhenotypeAge
	) {
		_alterers = alterers;
		_offspringSelectors = requireNonNull(offspringSelectors);
		_survivorsSelectors = requireNonNull(survivorsSelectors);
		_populationSize = requireNonNull(populationSize);
		_maxPhenotypeAge = requireNonNull(maxPhenotypeAge);

		// Create Genotype factory.
		final List<DoubleChromosome> ch = new ArrayList<>();

		// Alterers
		ch.addAll(_alterers.map(ParametersCodec::toChromosome).asList());

		// Offspring selector.
		ch.add(DoubleChromosome.of(0.0, 1.0));
		ch.addAll(_offspringSelectors.map(ParametersCodec::toChromosome).asList());

		// Survivors selector.
		ch.add(DoubleChromosome.of(0.0, 1.0));
		ch.addAll(_survivorsSelectors.map(ParametersCodec::toChromosome).asList());

		// Offspring fraction
		ch.add(DoubleChromosome.of(0.0, 1.0));

		// Population size.
		ch.add(DoubleChromosome.of(0.0, 1.0));

		// Phenotype age.
		ch.add(DoubleChromosome.of(0.0, 1.0));
		_encoding = Genotype.of(ISeq.of(ch));
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _encoding;
	}

	private static DoubleChromosome toChromosome(final Proxy<?> proxy) {
		return DoubleChromosome.of(0.0, 1.0, proxy.argsLength());
	}

	@Override
	public Function<Genotype<DoubleGene>, Parameters<G, C>> decoder() {
		return gt -> {
			ISeq<double[]> values = gt.toSeq().stream()
				.map(DoubleChromosome.class::cast)
				.map(DoubleChromosome::toArray)
				.collect(ISeq.toISeq());

			final ISeq<Alterer<G, C>> alterers =
				instances(_alterers, values.subSeq(0, _alterers.size()));
			values = values.subSeq(_alterers.size());

			final int offspringSelectorIndex = IntRange
				.of(_offspringSelectors.size()).intValue(values.get(0)[0]);
			values = values.subSeq(1);

			final Selector<G, C> offspringSelector =
				instance(offspringSelectorIndex, _offspringSelectors, values);
			values = values.subSeq(_offspringSelectors.size());

			final int survivorsSelectorIndex = IntRange
				.of(_survivorsSelectors.size()).intValue(values.get(0)[0]);
			values = values.subSeq(1);

			final Selector<G, C> survivorsSelector =
				instance(survivorsSelectorIndex, _survivorsSelectors, values);
			values = values.subSeq(_survivorsSelectors.size());


			final double offspringFraction = values.get(0)[0];
			values = values.subSeq(1);

			final int populationSize = _populationSize.intValue(values.get(0)[0]);
			values = values.subSeq(1);

			final long maxPhenotypeAge = _maxPhenotypeAge.intValue(values.get(0)[0]);

			return Parameters.of(
				alterers,
				offspringSelector,
				survivorsSelector,
				offspringFraction,
				populationSize,
				maxPhenotypeAge
			);
		};
	}

	private static <T> ISeq<T> instances(
		final Seq<Proxy<T>> proxies,
		final Seq<double[]> arguments
	) {
		assert proxies.size() == arguments.size() :
			format("Size not equals: %s != %s", proxies.size(), arguments.size());

		final Iterator<double[]> args = arguments.iterator();
		return proxies.stream()
			.flatMap(a -> a.factory()
				.apply(args.next())
				.map(Stream::of)
				.orElse(Stream.empty()))
			.collect(ISeq.toISeq());
	}

	private static <T> T instance(
		final int index,
		final Seq<Proxy<T>> proxy,
		final Seq<double[]> arguments
	) {
		return proxy.get(index).factory().apply(arguments.get(index)).get();
	}

}
