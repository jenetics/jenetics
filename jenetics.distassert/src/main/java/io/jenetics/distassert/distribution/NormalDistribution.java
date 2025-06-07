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

import org.apache.commons.numbers.gamma.Erfc;
import org.apache.commons.numbers.gamma.InverseErfc;

/**
 * Gaussian distribution implementation.
 *
 * @param mean the mean value of the distribution
 * @param stddev the standard deviation of the distribution
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
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
		return x -> {
			final double z = (x - mean)/stddev;
			return Math.exp(-0.5*z*z)/(Math.sqrt(2*Math.PI)*stddev);
		};
	}

	@Override
	public Cdf cdf() {
		return x -> {
			final double dev = x - mean;

			if (Math.abs(dev) > 40.0*stddev) {
				return dev < 0.0 ? 0.0 : 1.0;
			} else {
				return 0.5*Erfc.value(-dev/(stddev*SQRT2));
			}
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

			return mean - stddev*SQRT2*InverseErfc.value(2*p);
		};
	}

}
