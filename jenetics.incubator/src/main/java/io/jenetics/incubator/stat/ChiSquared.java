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
package io.jenetics.incubator.stat;

import org.apache.commons.statistics.distribution.ChiSquaredDistribution;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface ChiSquared extends HypothesisTester {

	@Override
	default Result test(final Histogram observation, final Distribution hypothesis) {
		final var maxChi2 = maxChi2(observation.degreesOfFreedom());
		final var chi2 = chi2(observation, hypothesis);

		if (chi2 > maxChi2) {
			return new Reject(
				hypothesis, observation,
				"Data doesn't follow %s: [max-chi2=%f, chi2=%f]."
					.formatted(hypothesis, maxChi2, chi2)
			);
		} else {
			return new Accept(
				hypothesis, observation,
				"Data follows %s: [max-chi2=%f, chi2=%f]."
					.formatted(hypothesis, maxChi2, chi2)
			);
		}
	}

	double pValue();

	double chi2(final Histogram observation, final Distribution hypothesis);

	default double maxChi2(final int degreesOfFreedom) {
		return ChiSquaredDistribution.of(degreesOfFreedom)
			.inverseCumulativeProbability(1 - pValue());
	}

}
