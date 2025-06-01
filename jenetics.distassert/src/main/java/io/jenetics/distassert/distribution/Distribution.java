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
package io.jenetics.distassert.distribution;

import org.apache.commons.numbers.rootfinder.BrentSolver;

import io.jenetics.distassert.observation.Interval;

/**
 * Defines the <i>domain</i>, <i>PDF</i> and <i>CDF</i> of a probability
 * distribution.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Distribution {

	/**
	 * Return the domain of the distribution. This is the interval, the
	 * distribution is valid.
	 *
	 * @return the distribution domain.
	 */
	default Interval domain() {
		return new Interval(-Double.MAX_VALUE, Double.MAX_VALUE);
	}

	/**
	 * Return a new instance of the <i>Probability Density Function</i> (PDF).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Probability_density_function">PDF</a>
	 *
	 * @return the <i>Probability Density Function</i>.
	 */
	Pdf pdf();

	/**
	 * Return a new instance of the <i>Cumulative Distribution Function</i> (CDF).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Cumulative_distribution_function">CDF</a>
	 *
	 * @return the <i>Cumulative Distribution Function</i>.
	 */
	Cdf cdf();

	/**
	 * Return the inverse CDF function. This function is created by using the
	 * {@link #cdf()} and a root finding algorithm for finding the inverse of
	 * a given CDF value.
	 *
	 * @return the inverse CDF function
	 */
	default InverseCdf icdf() {
		final var cdf = cdf();
		final var solver = new BrentSolver(0x1.0p-52, 0x1.0p-52, 0x1.0p-52);

		return p -> {
			if (p < 0 || p > 1) {
				throw new IllegalArgumentException(
					"The probability value must be in the range [0, 1], but was: " + p
				);
			}

			return solver.findRoot(
				x -> cdf.apply(x) - p,
				domain().min(),
				domain().max()
			);
		};
	}

}
