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
package io.jenetics.ext;

import java.util.Objects;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class WeightedSum<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	implements Function<Genotype<G>, N>
{
	private final ISeq<Function<? super Genotype<G>, ? extends N>> _finesses;

	public WeightedSum(
		final ISeq<Function<? super Genotype<G>, ? extends N>> finesses,
		final double[] weights
	) {
		finesses.forEach(Objects::requireNonNull);
		_finesses = finesses;
	}

	@Override
	public N apply(final Genotype<G> chromosomes) {
		return null;
	}

	public int arity() {
		return _finesses.size();
	}

	@SafeVarargs
	public static <G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	WeightedSum<G, N>
	of(final Function<? super Genotype<G>, ? extends N>... finesses) {
		return new WeightedSum<>(ISeq.of(finesses), null);
	}

}
