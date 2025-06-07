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
package io.jenetics.incubator.math;

/**
 * Simple gamma function implementation based on
 * <a href="https://www.johndcook.com/blog/csharp_gamma/">
 *     https://www.johndcook.com/blog/csharp_gamma/</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
public final class Gamma {

	private static final double HALF_LOG_2PI = 0.91893853320467274178032973640562;

	private static final double EULER_GAMMA = 0.577215664901532860606512090;

	private static final double[] P = {
		-1.71618513886549492533811E+0,
		2.47656508055759199108314E+1,
		-3.79804256470945635097577E+2,
		6.29331155312818442661052E+2,
		8.66966202790413211295064E+2,
		-3.14512729688483675254357E+4,
		-3.61444134186911729807069E+4,
		6.64561438202405440627855E+4
	};

	private static final double[] Q = {
		-3.08402300119738975254353E+1,
		3.15350626979604161529144E+2,
		-1.01515636749021914166146E+3,
		-3.10777167157231109440444E+3,
		2.25381184209801510330112E+4,
		4.75584627752788110767815E+3,
		-1.34659959864969306392456E+5,
		-1.15132259675553483497211E+5
	};

	private static final double[] C = {
		1.0/12.0,
		-1.0/360.0,
		1.0/1260.0,
		-1.0/1680.0,
		1.0/1188.0,
		-691.0/360360.0,
		1.0/156.0,
		-3617.0/122400.0
	};

	private Gamma() {
	}

	public static double gamma(final double x) {
		if (Double.isNaN(x) || x <= 0.0) {
			throw new IllegalArgumentException("Input value must be positive: " + x);
		}

		// Split the function domain into three intervals:
		// (0, 0.001), [0.001, 12), and (12, infinity)

		// (0, 0.001)
		// For small x, 1/Gamma(x) has power series x + gamma x^2  - ...
		// So in this range, 1/Gamma(x) = x + gamma x^2 with error on the order of x^3.
		// The relative error over this interval is less than 6e-7.
		if (x < 0.001) {
			return 1.0/(x*(1.0 + EULER_GAMMA*x));
		}

		// [0.001, 12)
		if (x < 12.0) {
			// The algorithm directly approximates gamma over (1,2) and uses
			// reduction identities to reduce other arguments to this interval.

			int n = 0;
			double y = x;

			// Add or subtract integers as necessary to bring y into (1,2)
			// Will correct for this below
			if (x < 1.0) {
				y += 1.0;
			} else {
				n = (int)(Math.floor(y)) - 1;  // will use n later
				y -= n;
			}

			double num = 0.0;
			double den = 1.0;
			int i;

			double z = y - 1;
			for (i = 0; i < 8; ++i) {
				num = (num + P[i])*z;
				den = den*z + Q[i];
			}
			double result = num/den + 1.0;

			// Apply correction if the argument was not initially in (1, 2)
			if (x < 1.0) {
				// Use identity gamma(z) = gamma(z+1)/z
				// The variable "result" now holds gamma of the original y + 1
				// Thus we use y-1 to get back the original y.
				result /= (y-1.0);
			} else {
				// Use the identity gamma(z+n) = z*(z+1)* ... *(z+n-1)*gamma(z)
				for (i = 0; i < n; ++i) {
					result *= y++;
				}
			}

			return result;
		}

		// Third interval: [12, infinity)
		if (x > 171.624) {
			// Correct answer too large to display.
			return Double.POSITIVE_INFINITY;
		}

		return Math.exp(logGamma(x));
	}

	public static double logGamma(final double x) {
		if (Double.isNaN(x) || x <= 0.0) {
			throw new IllegalArgumentException("Input value must be positive: " + x);
		}

		if (x < 12.0) {
			return Math.log(Math.abs(gamma(x)));
		}

		// Abramowitz and Stegun 6.1.41
		// Asymptotic series should be good to at least 11 or 12 figures
		// For error analysis, see Whittiker and Watson
		// A Course in Modern Analysis (1927), page 252

		final double z = 1.0/(x*x);
		double sum = C[7];
		for (int i = 6; i >= 0; i--) {
			sum *= z;
			sum += C[i];
		}

		return (x - 0.5)*Math.log(x) - x + HALF_LOG_2PI + sum/x;
	}


}
