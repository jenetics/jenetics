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

import io.jenetics.incubator.math.Erf;

/**
 * Gaussian distribution implementation.
 *
 * @param mean the mean value of the distribution
 * @param stddev the standard deviation of the distribution
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record NormalDistribution(double mean, double stddev)
	implements Distribution
{
	private static final double SQRT2 = Math.sqrt(2);
	private static final double HALF_LOG_TAU = 0.5*Math.log(Math.TAU);

	public NormalDistribution {
		if (stddev <= 0) {
			throw new IllegalArgumentException(
				"Stddev must be > 0, but was %f.".formatted(stddev)
			);
		}
	}

	@Override
	public Pdf pdf() {
		return this::pdf;
	}

	public double pdf(final double x) {
		final double x0 = x - mean;
		final double x1 = x0/stddev;
		final double x2 = -0.5*x1*x1 - Math.log(stddev) + HALF_LOG_TAU;
		return Math.exp(x2);
	}

	@Override
	public Cdf cdf() {
		return this::cdf;
	}

	public double cdf(final double x) {
		final double dev = x - mean;

		if (Math.abs(dev) > 40.0*stddev) {
			return dev < 0.0 ? 0.0 : 1.0;
		} else {
			return 0.5 * Erf.erfc(-dev/(stddev*SQRT2));
		}
	}

}
