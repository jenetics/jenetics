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
package io.jenetics.testfixtures.stat;

import static java.util.Objects.requireNonNull;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import io.jenetics.internal.util.Requires;

/**
 *
 * @param p the p-value
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public record PearsonChiSquared(Distribution hypothesis, double p)
	implements StatisticalHypothesis
{

	public PearsonChiSquared {
		requireNonNull(hypothesis);
		Requires.probability(p);
	}

	public PearsonChiSquared(Distribution hypothesis) {
		this(hypothesis, 0.05);
	}

	@Override
	public Result test(final Histogram observation) {
		final var maxChi2 = maxChi2(observation.degreesOfFreedom());
		final var chi2 = chi2(observation);

		if (chi2 > maxChi2) {

		}

		return null;
	}

	double chi2(final Histogram hist) {
		requireNonNull(hist);

		final var count = hist.sampleCount();
		final var cdf = hypothesis.cdf();

		final var chi2 = hist.bins()
			.map(bin -> new double[] {
					bin.count()*bin.count(),
					bin.probability(cdf)*count
				}
			)
			.filter(values -> values[0] != 0.0)
			.mapToDouble(values -> values[0]/values[1])
			.sum();

		return chi2 - count;
	}

	private double maxChi2(final int degreesOfFreedom) {
		return new ChiSquaredDistribution(degreesOfFreedom)
			.inverseCumulativeProbability(1 - p);
	}

	public static void main(String[] args) {
		final var hypothesis = new PearsonChiSquared(
			new NormalDistribution(0, 1),
			0.005
		);

		System.out.println(hypothesis.maxChi2(5));
	}

}
