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

import io.jenetics.util.DoubleRange;

import static java.lang.Math.clamp;
import static java.lang.Math.nextDown;
import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

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
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.1
 */
public class GaussianMutator<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	private static final int MAX_RETRIES = 100;

	/**
	 * The parameters which define the <em>shape</em> of distribution of the
	 * created (mutated) values. The given values are given for a <em>standard</em>
	 * normal distribution. A gene with a range of {@code [0, 10]} and a desired
	 * mean value of {@code 5}, will have a {@code DistShape} mean value of zero.
	 *
	 * @param mean the mean value of the <em>normal</em> standard distribution
	 * @param stddev the expected standard deviation for the gene's boundary
	 *        (min and max) values
	 */
	public record DistShape(double mean, double stddev) {
		double next(final double min, final double max, final RandomGenerator random) {
			final var factor = (max - min)/2.0;
			return random.nextGaussian(mean*factor + factor, stddev*factor);
		}

		double mean(double min, double max) {
			final var factor = (max - min)/2.0;
			return factor*mean + factor;
		}
	}

	private final DistShape shape;

	public GaussianMutator(final double probability, final DistShape shape) {
		super(probability);
		this.shape = requireNonNull(shape);
	}

	public GaussianMutator(final DistShape shape) {
		this(DEFAULT_ALTER_PROBABILITY, shape);
	}

	public GaussianMutator(final double probability) {
		this(probability, new DistShape(0, 1.0/3.0));
	}

	public GaussianMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	@Override
	protected G mutate(final G gene, final RandomGenerator random) {
		return gene.isValid() ? mutate0(gene, random) : gene;
	}

	private G mutate0(final G gene, final RandomGenerator random) {
		final double min = gene.min().doubleValue();
		final double max = gene.max().doubleValue();

		int retries = 0;
		double next = shape.next(min, max, random);
		while (retries++ < MAX_RETRIES && next < min || next >= max) {
			next = shape.next(min, max, random);
		}

		//System.out.println("RETRY: " + retries);

		if (retries < MAX_RETRIES) {
			return gene.newInstance(next);
		} else {
			return gene;
		}

		/*
		final double min = gene.min().doubleValue();
		final double max = gene.max().doubleValue();
		final double mean = (max - min)/2.0 + min;
		// min =  6*stddev, für 3*stddev Abstand vom Mittelwert.
		// min = 10*stddev, für 5*stddev Abstand vom Mittelwert.
		final double stddev = (max - min)*0.1;

		final double gaussian = random.nextGaussian(mean, (max - min)*stddev);
		if (gaussian <= min || gaussian >= max) {
			//System.out.println(count + ": " + gaussian + " --> " + value + ", " + this.stddev);
			return gene;
		}
		return gene.newInstance(clamp(gaussian, min, nextDown(max)));
		 */
	}

}
