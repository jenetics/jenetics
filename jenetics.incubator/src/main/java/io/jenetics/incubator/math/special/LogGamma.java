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
package io.jenetics.incubator.math.special;

/**
 * Simple <em>log</em> gamma function implementation based on
 * <a href="https://www.johndcook.com/blog/csharp_gamma/">
 *     https://www.johndcook.com/blog/csharp_gamma/</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class LogGamma {

	private static final double HALF_LOG_2PI = 0.91893853320467274178032973640562;

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

	private LogGamma() {
	}

	public static double apply(final double x) {
		if (Double.isNaN(x) || x <= 0.0) {
			throw new IllegalArgumentException("Input value must be positive: " + x);
		}

		if (x < 12.0) {
			return Math.log(Math.abs(Gamma.gamma(x)));
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
