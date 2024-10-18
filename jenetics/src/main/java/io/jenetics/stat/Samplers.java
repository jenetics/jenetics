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
package io.jenetics.stat;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import io.jenetics.util.DoubleRange;

/**
 * This class defines some default samplers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class Samplers {

	private Samplers() {
	}

	/**
	 * Return a new sampler for a <em>linear</em> distribution with the given
	 * {@code mean} value, when creating sample points for the range
	 * {@code [0, 1)}.
	 * <p>
	 *	<img src="doc-files/LinearDistributionPDF.svg" width="450"
	 *	     alt="Linear distribution sampler" >
	 *
	 * @param mean the mean value of the samplers distribution
	 * @return a new linear sampler with the given {@code mean} value
	 * @throws IllegalArgumentException if the given {@code mean} value is not
	 *         within the range {@code [0, 1)}
	 */
	public static Sampler linear(final double mean) {
		if (mean < 0 || mean >= 1) {
			throw new IllegalArgumentException(
				"Mean value not within allowed range [0, 1): %f."
					.formatted(mean)
			);
		}

		final class Range {
			static final double MIN = 0.0;
			static final double MAX = Math.nextDown(1.0);
			static final double LIMIT1 = 1.0 - 1.0/sqrt(2);
			static final double LIMIT2 = 1.0/sqrt(2);
			static double clamp(final double value) {
				return Math.clamp(value, MIN, MAX);
			}
		}

		if (Double.compare(mean, 0) == 0) {
			return (random, range) -> Range.MIN;
		} else if (mean == Range.MAX) {
			return (random, range) -> Range.MAX* range.max();
		}

		final double b, m;
		if (mean < Range.LIMIT1) {
			b = (2 - sqrt(2))/mean;
			m = -pow(b, 2)/2;
		} else if (mean < Range.LIMIT2) {
			b = (pow(mean, 2) - 0.5)/(pow(mean, 2) - mean);
			m = 2 - 2*b;
		} else if (mean < 1) {
			b = 0.5*(1 - pow(((2 - sqrt(2))/(1 - mean) - 1), 2));
			m = -b + sqrt(1 - 2*b) + 1;
		} else {
			b = m = 1;
		}

		return (random, range) -> {
			final var r = random.nextDouble();

			final double sample;
			if (mean < 0.5) {
				sample = Range.clamp((-b + sqrt(b*b + 2*r*m))/m);
			} else if (mean == 0.5) {
				sample = r;
			} else {
				sample = Range.clamp((b - sqrt(b*b + 2*m*(r - 1 + b + 0.5*m)))/-m);
			}

			return stretch(sample, range);
		};
	}

	/**
	 * Create a new sampler for a triangle distribution with the given
	 * parameters. All parameters must be within the range {@code [0, 1]}.
	 * <p>
	 *	<img src="doc-files/TriangularDistributionPDF.svg" width="450"
	 *	     alt="Triangle distribution sampler" >
	 *
	 * @see #triangular(double)
	 *
	 * @param a the start point of the triangle
	 * @param c the middle point of the triangle
	 * @param b the end point of the triangle
	 * @return a new triangle distribution sampler
	 * @throws IllegalArgumentException if one of the parameters is not within
	 *         the range {@code [0, 1]} or {@code b <= a || c > b || c < a}
	 */
	public static Sampler
	triangular(final double a, final double c, final double b) {
		if (a < 0 || b < 0 || c < 0  ||
			a > 1 || b > 1 || c > 1 ||
			b <= a || c > b || c < a)
		{
			throw new IllegalArgumentException(
				"Invalid triangular: [a=%f, c=%f, b=%f].".formatted(a, c, b)
			);
		}

		final double fc = (c - a)/(b - a);

		return (random, range) -> {
			final var r = random.nextDouble();

			final double sample;
			if (Double.compare(r, 0.0) == 0) {
				sample = 0;
			} else if (r < fc) {
				sample = a + sqrt(r*(b - a)*(c - a));
			} else {
				sample = b - sqrt((1 - r)*(b - a)*(b - c));
			}

			return stretch(sample, range);
		};
	}

	/**
	 * Return a new sampler for a <em>normalized</em> triangle distribution
	 * with the points {@code [0, c, 1]}.
	 *
	 * @see #triangular(double, double, double)
	 *
	 * @param c the middle point of the triangle within the range {@code [0, 1}
	 * @return a new triangle distribution sampler
	 * @throws IllegalArgumentException if c not within {@code [0, 1]}
	 */
	public static Sampler triangular(final double c) {
		return triangular(0, c, 1.0);
	}

	private static double stretch(final double sample, DoubleRange range) {
		return range.min() + sample*(range.max() - range.min());
	}

}

