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
package org.jenetics.stat;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import org.jenetics.util.StaticObject;

/**
 * Some statistical special functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-04-27 $</em>
 */
final class math extends StaticObject {
	private math() {}


	/**
	 * Uses Lanczos approximation formula. See Numerical Recipes 6.1.
	 *
	 * @param x
	 * @return the log gamma value.
	 */
	static double logGamma(final double x) {
		final double ser = 1.0 + 76.18009173 /
							(x + 0) - 86.50532033 /
							(x + 1) + 24.01409822 /
							(x + 2) - 1.231739516 /
							(x + 3) + 0.00120858003 /
							(x + 4) - 0.00000536382 /
							(x + 5);

		return (x - 0.5)*log(x + 4.5) - (x + 4.5) + log(ser*sqrt(2 * PI));
	}

	public static double gamma(final double x) {
		return exp(logGamma(x));
	}

	public static double Γ(final double x) {
		return gamma(x);
	}



	/**
	 * Return the <i>error function</i> of {@code z}. The fractional error of
	 * this implementation is less than 1.2E-7.
	 *
	 * @param z the value to calculate the error function for.
	 * @return the error function for {@code z}.
	 */
	public static double erf(final double z) {
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
	 * Return φ(x), the standard Gaussian pdf.
	 *
	 * @see #φ(double)
	 * @param x the value to calculate φ for.
	 * @return the φ value for x.
	 */
	public static double phi(final double x) {
		return Math.exp(-x*x/2.0) / Math.sqrt(2.0*Math.PI);
	}

	/**
	 * Return φ(x), the standard Gaussian pdf.
	 *
	 * @see #phi(double)
	 * @param x the value to calculate φ for.
	 * @return the φ value for x.
	 */
	public static double φ(final double x) {
		return phi(x);
	}

	/**
	 * Return φ(x, µ, σ), the standard Gaussian pdf with mean µ and stddev σ.
	 *
	 * @see #phi(double, double, double)
	 * @param x the value to calculate φ for.
	 * @param mu the mean value.
	 * @param sigma the stddev.
	 * @return the φ value for x.
	 */
	public static double phi(final double x, final double mu, final double sigma) {
		return phi((x - mu)/sigma)/sigma;
	}

	/**
	 * Return φ(x, µ, σ), the standard Gaussian pdf with mean µ and stddev σ.
	 *
	 * @see #phi(double, double, double)
	 * @param x the value to calculate φ for.
	 * @param µ the mean value.
	 * @param σ the stddev.
	 * @return the φ value for x.
	 */
	public static double φ(final double x, final double µ, final double σ) {
		return phi(x, µ, σ);
	}

	/**
	 * Return Φ(z), the standard Gaussian cdf using Taylor approximation.
	 *
	 * @param z the value to calculate Φ for.
	 * @return the Φ for value z.
	 */
	public static double Phi(final double z) {
		if (z < -8.0) {
			return 0.0;
		}
		if (z >  8.0) {
			return 1.0;
		}

		double s = 0.0;
		double t = z;
		for (int i = 3; s + t != s; i += 2) {
			s  = s + t;
			t = t*z*z/i;
		}
		return 0.5 + s*phi(z);
	}

	/**
	 * Return Φ(z), the standard Gaussian cdf using Taylor approximation.
	 *
	 * @param z the value to calculate Φ for.
	 * @return the Φ for value z.
	 */
	public static double Φ(final double z) {
		return Phi(z);
	}

	/**
	 * Return Φ(z, µ, σ), the standard Gaussian cdf with mean µ and stddev σ.
	 *
	 * @see #phi(double, double, double)
	 * @param z the value to calculate Φ for.
	 * @param mu the mean value.
	 * @param sigma the stddev.
	 * @return the φ value for x.
	 */
	public static double Phi(final double z, final double mu, final double sigma) {
		return Phi((z - mu)/sigma);
	}

	/**
	 * Return Φ(z, µ, σ), the standard Gaussian cdf with mean µ and stddev σ.
	 *
	 * @see #phi(double, double, double)
	 * @param z the value to calculate Φ for.
	 * @param µ the mean value.
	 * @param σ the stddev.
	 * @return the φ value for x.
	 */
	public static double Φ(final double z, final double µ, final double σ) {
		return Phi(z, µ, σ);
	}

}












