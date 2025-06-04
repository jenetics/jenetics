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
 * This mutator picks a new value based on a Gaussian distribution, defined by
 * the {@link Shape} parameter of the mutator.
 * <br>
 * <img src="doc-files/gaussian-mutator-sigma.svg" alt="Sigma graph" width="500"/>
 * <br>
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
	 * The parameters which define the <em>shape</em> of gaussian distribution
	 * of new gene values.
	 * <p>
	 * <b>shift</b><br>
	 * The {@code shift} value shifts the mean value of the distribution. Positive
	 * values shifts it right and negative values left. The {@code shift} value
	 * must be within the range of {@code [-1, 1]}. From the {@code shift}
	 * parameter the actual µ value is calculated as follows:
	 * {@code µ = (shift + 1)((max - min)/2)}.
	 * <br>
	 * <img src="doc-files/gaussian-mutator-shift.svg" alt="Shift graph" width="500"/>
	 * <br>
	 * <b>sigma</b><br>
	 * The {@code sigma} value spreads <em>stretches</em> and <em>compresses</em>
	 * the distribution. The {@code shift} value must be within the range of
	 * {@code [0.1, 5]}. From the {@code sigma} parameter the actual σ value is
	 * calculated as follows: {@code σ = ((max - min)/2)/sigma}.
	 * <br>
	 * <img src="doc-files/gaussian-mutator-sigma.svg" alt="Sigma graph" width="500"/>
	 * <br>
	 *
	 * @param shift the shift parameter, S, determining the mean value of the
	 *         crated mutation value distribution
	 * @param sigma the sigma parameter, Σ, determining the standard deviation of
	 *        the created mutation value distribution
	 * @since !__version__!
	 * @version !__version__!
	 */
	public record Shape(double shift, double sigma) implements Sampler {

		/**
		 * Create a new mutation distribution shape.
		 *
		 * @param shift the shift parameter, S, determining the mean value of the
		 *         crated mutation value distribution
		 * @param sigma the sigma parameter, Σ, determining the standard deviation of
		 *        the created mutation value distribution
		 * @throws IllegalArgumentException if {@code shift < -1 || shift > 1} of
		 *         {@code sigma < 0.1 || sigma > 5}
		 */
		public Shape {
			if (shift < -1 || shift > 1) {
				throw new IllegalArgumentException(
					"Shift must be  within the range [-1, 1]: " + shift
				);
			}
			if (sigma < 0.1 || sigma > 5) {
				throw new IllegalArgumentException(
					"Sigma must be within the range [0.1, 5]: " + sigma
				);
			}
		}

		@Override
		public double sample(final RandomGenerator random, final DoubleRange range) {
			final var sig = (range.max() - range.min())/2.0;
			final var mean = sig + sig*shift;
			final var stddev = sig/sigma;

			return Samplers.gaussian(mean, stddev).sample(random, range);
		}

		double stddev(final DoubleRange range) {
			return ((range.max() - range.min())/2.0)/sigma;
		}

		double mean(final DoubleRange range) {
			return (shift + 1)*((range.max() - range.min())/2.0);
		}

	}

	/**
	 * The default shape of the mutator: {@code Shape[shift=0, sigma=1]}.
	 */
	public static final Shape DEFAULT_SHAPE = new Shape(0, 1);

	/**
	 * Create a new Gaussian mutator with the given parameter.
	 *
	 * @param probability the mutation probabilities
	 * @param shape the <em>shape</em> of the mutation value distribution
	 */
	public GaussianMutator(final double probability, final Shape shape) {
		super(probability, shape);
	}

	/**
	 * Create a new Gaussian mutator with the given mutation value distribution
	 * shape and the default mutation probability, {@link #DEFAULT_ALTER_PROBABILITY}.
	 *
	 * @param shape the mutation value distribution shape
	 */
	public GaussianMutator(final Shape shape) {
		this(DEFAULT_ALTER_PROBABILITY, shape);
	}

	/**
	 * Crate a new Gaussian mutator with the given mutation probability and the
	 * default shape, {@link #DEFAULT_SHAPE}.
	 *
	 * @param probability the mutation probability
	 */
	public GaussianMutator(final double probability) {
		this(probability, DEFAULT_SHAPE);
	}

	/**
	 * Create a new Gaussian mutator with the default mutation probability,
	 * {@link #DEFAULT_ALTER_PROBABILITY}, and default distribution shape,
	 * {@link #DEFAULT_SHAPE}.
	 */
	public GaussianMutator() {
		this(DEFAULT_ALTER_PROBABILITY, DEFAULT_SHAPE);
	}

}
