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

import static java.lang.Math.nextDown;
import static java.lang.String.format;
import static io.jenetics.internal.math.Basics.clamp;

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

	public GaussianMutator(final double probability) {
		super(probability);
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
		final double std = (max - min)*0.25;

		final double value = gene.doubleValue();
		final double gaussian = random.nextGaussian();
		return gene.newInstance(clamp(gaussian*std + value, min, nextDown(max)));
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
