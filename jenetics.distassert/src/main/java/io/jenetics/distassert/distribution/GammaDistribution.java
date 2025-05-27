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

import org.apache.commons.numbers.gamma.RegularizedGamma;

import io.jenetics.distassert.Interval;

/**
 * Implementation of the gamma distribution.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Gamma_distribution">Gamma distribution (Wikipedia)</a>
 * @see <a href="https://mathworld.wolfram.com/GammaDistribution.html">Gamma distribution (MathWorld)</a>
 *
 * @param shape shape parameter
 * @param scale scale parameter
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record GammaDistribution(double shape, double scale)
	implements Distribution
{

	@Override
	public Interval domain() {
		return new Interval(0.0, Double.MAX_VALUE);
	}

	@Override
	public Pdf pdf() {
		return x -> {
			if (x <= 0) {
				if (x == 0 && shape <= 1) {
					return shape == 1
						? 1.0/scale
						: Double.POSITIVE_INFINITY;
				} else {
					return 0;
				}
			} else {
				return RegularizedGamma.P.derivative(shape, x/scale)/scale;
			}
		};
	}

	@Override
	public Cdf cdf() {
		return x -> {
			if (x <= 0.0) {
				return 0;
			} else if (x >= Double.MAX_VALUE) {
				return 1;
			} else {
				return RegularizedGamma.P.value(shape, x/scale);
			}
		};
	}

}
