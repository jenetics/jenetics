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
package io.jenetics;

import static java.util.Objects.requireNonNull;

import java.util.random.RandomGenerator;

import io.jenetics.stat.Sampler;
import io.jenetics.util.DoubleRange;

/**
 * A mutator which replaces a gene (mutates it) with a value created by a
 * {@link Sampler}.
 *
 * @param <G> the gene type
 * @param <C> the allele type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class SamplerMutator<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	protected final Sampler sampler;

	/**
	 * Create a new mutator with the given mutation {@code probability} and
	 * gene {@code sampler}.
	 *
	 * @param probability the mutation probability
	 * @param sampler the gene sampler for creating new gene values
	 */
	public SamplerMutator(final double probability, final Sampler sampler) {
		super(probability);
		this.sampler = requireNonNull(sampler);
	}

	/**
	 * Create a new mutator with the default mutation probability
	 * {@link #DEFAULT_ALTER_PROBABILITY} and gene {@code sampler}.
	 *
	 * @param sampler the gene sampler for creating new gene values
	 */
	public SamplerMutator(final Sampler sampler) {
		this(DEFAULT_ALTER_PROBABILITY, sampler);
	}

	/**
	 * Create a new mutator with the given mutation {@code probability} and
	 * uniform gene sample, {@link Sampler#UNIFORM}.
	 *
	 * @param probability the mutation probability
	 */
	public SamplerMutator(final double probability) {
		this(probability, Sampler.UNIFORM);
	}

	@Override
	protected G mutate(final G gene, final RandomGenerator random) {
		final var range = new DoubleRange(
			gene.min().doubleValue(),
			gene.max().doubleValue()
		);
		final var next = sampler.sample(random, range);

		return Double.isNaN(next) ? gene : gene.newInstance(next);
	}

}
