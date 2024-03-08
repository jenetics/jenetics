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

import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

import io.jenetics.util.DoubleRange;

/**
 * Gaussian distribution implementation.
 *
 * @param mean the mean value of the distribution
 * @param stddev the standard deviation of the distribution
 */
public record NormalDistribution(double mean, double stddev)
	implements Distribution
{

	@Override
	public DoubleRange domain() {
		return DoubleRange.of(-Double.MAX_VALUE, Double.MAX_VALUE);
	}

	@Override
	public Pdf pdf() {
		return x -> {
			final double x0 = x - mean;
			final double x1 = x0/ stddev;
			final double x2 = -0.5 * x1 * x1 - Math.log(stddev) + 0.5 * Math.log(6.283185307179586);
			return Math.exp(x2);
		};
	}

	@Override
	public Cdf cdf() {
		return x -> {
			double dev = x - this.mean;
			if (FastMath.abs(dev) > 40.0 * this.stddev) {
				return dev < 0.0 ? 0.0 : 1.0;
			} else {
				return 0.5 * Erf.erfc(-dev / (stddev * Math.sqrt(2)));
			}
		};
	}

	/*
	public double cumulativeProbability(double x) {
		double dev = x - this.mean;
		if (FastMath.abs(dev) > 40.0 * this.stddev) {
			return dev < 0.0 ? 0.0 : 1.0;
		} else {
			return 0.5 * Erf.erfc(-dev / (this.stddev * SQRT2));
		}
	}
	 */

}
