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

import io.jenetics.distassert.observation.Interval;

/**
 * <a href="http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29">
 * Uniform distribution</a> class.
 *
 * @see LinearDistribution
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record UniformDistribution(Interval domain) implements Distribution {

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="../doc-files/uniform-pdf.gif"
	 *     alt="f(x)=\left\{\begin{matrix}
	 *          \frac{1}{max-min} & for & x \in [min, max] \\
	 *          0 & & otherwise \\
	 *          \end{matrix}\right."
	 * >
	 * </p>
	 *
	 */
	@Override
	public Pdf pdf() {
		final var pdf = 1.0/domain.size();
		return x -> (x >= domain.min() && x <= domain.max()) ? pdf : 0.0;
	}

	/**
	 * Return a new CDF object.
	 *
	 * <p>
	 * <img
	 *     src="../doc-files/uniform-cdf.gif"
	 *     alt="f(x)=\left\{\begin{matrix}
	 *         0 & for & x < min \\
	 *         \frac{x-min}{max-min} & for & x \in [min, max] \\
	 *         1 & for & x > max  \\
	 *         \end{matrix}\right."
	 * >
	 * </p>
	 *
	 */
	@Override
	public Cdf cdf() {
		return x -> {
			final double divisor = domain.max() - domain.min();

			double result;
			if (x < domain.min()) {
				result = 0.0;
			} else if (x > domain.max()) {
				result = 1.0;
			} else {
				result = (x - domain.min())/divisor;
			}

			return result;
		};
	}

	@Override
	public InverseCdf icdf() {
		return p -> {
			if (p < 0 || p > 1) {
				throw new IllegalArgumentException(
					"The probability value must be in the range [0, 1], but was: " + p
				);
			}

			return p == 1
				? domain.max()
				: (domain.max() - domain.min())*p + domain().min();
		};
	}

}
