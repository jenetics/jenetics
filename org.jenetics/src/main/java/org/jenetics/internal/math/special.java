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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.math;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

import org.jenetics.util.StaticObject;

/**
 * Some special functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public final class special extends StaticObject {
	private special() {}

	/**
	 * Return the <i>error function</i> of {@code z}. The fractional error
	 * of this implementation is less than 1.2E-7.
	 *
	 * @param z the value to calculate the error function for.
	 * @return the error function for {@code z}.
	 */
	static double erf(final double z) {
		final double t = 1.0/(1.0 + 0.5*abs(z));

		// Horner's method
		final double result = 1 - t*exp(
				-z*z - 1.26551223 +
				t*( 1.00002368 +
				t*( 0.37409196 +
				t*( 0.09678418 +
				t*(-0.18628806 +
				t*( 0.27886807 +
				t*(-1.13520398 +
				t*( 1.48851587 +
				t*(-0.82215223 +
				t*(0.17087277))))))))));

		return z >= 0 ? result : -result;
	}

	/**
	 * TODO: Implement gamma function.
	 *
	 * @param x
	 * @return
	 */
	static double Γ(final double x) {
		return x;
	}

}
