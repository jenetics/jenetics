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

import java.util.random.RandomGenerator;

import io.jenetics.stat.Sampler;
import io.jenetics.stat.Samplers;
import io.jenetics.util.DoubleRange;

/**
 * The GaussianMutator class performs the mutation of a {@link NumericGene}.
 * This mutator picks a new value based on a Gaussian distribution around the
 * current value of the gene. The variance of the new value (before clipping to
 * the allowed gene range) will be
 * <p>
 * <img
 *     src="doc-files/gaussian-mutator-var.svg"
 *     alt="\hat{\sigma }^2 = \left ( \frac{ g_{max} - g_{min} }{4}\right )^2"
 * >
 * </p>
 * The new value will be cropped to the gene's boundaries.
 *
 * @param <G> the gene type
 * @param <C> the allele type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
public class GaussianMutator<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	extends SamplerMutator<G, C>
{

	/**
	 * The parameters which define the <em>shape</em> of distribution of the
	 * created (mutated) values. The given values are given for a <em>standard</em>
	 * normal distribution. A gene with a range of {@code [0, 10]} and a desired
	 * mean value of {@code 5}, will have a {@code DistShape} mean value of zero.
	 *
	 * @param shift the mean value of the <em>normal</em> standard distribution
	 * @param sigmas the expected standard deviation for the gene's boundary
	 *        (min and max) values
	 * @since !__version__!
	 */
	public record DistShape(double shift, double sigmas) implements Sampler {

		public DistShape {
			if (sigmas <= 0) {
				throw new IllegalArgumentException(
					"Standard deviation must be greater than zero: " + sigmas
				);
			}
		}

		@Override
		public double sample(final RandomGenerator random, final DoubleRange range) {
			final var sigma = (range.max() - range.min())/2.0;
			final var mean = sigma + sigma*shift;
			final var stddev = sigma/sigmas;

			return Samplers.gaussian(mean, stddev).sample(random, range);
		}

		double stddev(final DoubleRange range) {
			final var sigma = (range.max() - range.min())/2.0;
			return sigma/sigmas;
		}

		double mean(final DoubleRange range) {
			final var sigma = (range.max() - range.min())/2.0;
			return sigma + sigma*shift;
		}

	}

	public GaussianMutator(final double probability, final DistShape shape) {
		super(probability, shape);
	}

	public GaussianMutator(final DistShape shape) {
		this(DEFAULT_ALTER_PROBABILITY, shape);
	}

	public GaussianMutator(final double probability) {
		this(probability, new DistShape(0, 1));
	}

	public GaussianMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	@Override
	protected G mutate(final G gene, final RandomGenerator random) {
		return gene.isValid() ? mutate0(gene, random) : gene;
	}

	private G mutate0(final G gene, final RandomGenerator random) {
		final var range = new DoubleRange(
			gene.min().doubleValue(),
			gene.max().doubleValue()
		);

		final var next = sampler.sample(random, range);
		return Double.isNaN(next) ? gene : gene.newInstance(next);
	}

}
