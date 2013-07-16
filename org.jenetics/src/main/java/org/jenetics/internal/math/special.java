/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
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
