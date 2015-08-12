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

import static org.jenetics.internal.util.reflect.create;

import java.lang.reflect.Constructor;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class CtorCodec<T> implements Codec<T, DoubleGene> {

	private final Factory<Genotype<DoubleGene>> _encoding;
	private final Function<Genotype<DoubleGene>, T> _decoder;

	public CtorCodec(
		final Class<T> type,
		final ISeq<DoubleRange> ranges
	) {
		_encoding = Genotype.of(DoubleChromosome.of(
			ranges.stream()
				.map(DoubleGene::of)
				.toArray(DoubleGene[]::new)
		));

		final ISeq<DoubleFunction<Object>> parameters = ranges
			.map(r -> (DoubleFunction<Object>) Double::valueOf);

		final Ctor<T> ctor = Ctor.of(
			type,
			parameters
		);

		_decoder = gt -> ctor.cons(
			gt.toSeq().stream()
				.mapToDouble(c -> c.getGene().doubleValue())
				.toArray()
		);
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _encoding;
	}

	@Override
	public Function<Genotype<DoubleGene>, T> decoder() {
		return _decoder;
	}


	public static <T> Codec<T, DoubleGene> of(
		final Class<T> type,
		final DoubleRange... params
	) {
		final Constructor<T> ctor = ctor(
			type,
			Stream.of(params)
				.map(p -> Double.class)
				.toArray(Class[]::new)
		);

		return Codec.of(
			Genotype.of(DoubleChromosome.of(
				Stream.of(params)
					.map(DoubleGene::of)
					.toArray(DoubleGene[]::new)
			)),
			gt -> {
				final Object[] args =  gt.toSeq()
					.map(c -> c.getGene().getAllele())
					.toArray();

				return create(ctor, args);
			}
		);
	}

	private static <T> Constructor<T> ctor(
		final Class<T> type,
		final Class<?>... parameterTypes
	) {
		try {
			return type.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
				"Invalid constructor parameters.", e
			);
		}
	}


	public static <T> Codec<T, DoubleGene> of(
		final Class<T> type,
		final IntRange... params
	) {
		final Constructor<T> ctor = ctor(
			type,
			Stream.of(params)
				.map(p -> Integer.class)
				.toArray(Class[]::new)
		);

		return Codec.of(
			Genotype.of(DoubleChromosome.of(
				Stream.of(params)
					.map(p -> DoubleGene.of(p.getMin(), p.getMax()))
					.toArray(DoubleGene[]::new)
			)),
			gt -> {
				final Object[] args =  gt.toSeq()
					.map(c -> c.getGene().getAllele().intValue())
					.toArray();

				return create(ctor, args);
			}
		);
	}

}
