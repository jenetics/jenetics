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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import io.jenetics.distassert.observation.Interval;

/**
 * <p>This distribution has the following cdf.</p>
 * <p><img src="../doc-files/LinearDistribution.png" alt="Distribution"></p>
 * <p>
 * The only restriction is that the integral of the cdf must be one.
 * </p>
 * <p>
 * <img src="../doc-files/linear-precondition.gif"
 *      alt="\int_{x_1}^{x_2}\left(
 *             \\underset{k} {\\underbrace {\frac{y_2-y_1}{x_2-x_1}}} \cdot x +
 *             \\underset{d}{\\underbrace {y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1}}
 *           \right)\mathrm{d}x = 1"
 *  >
 *  </p>
 *
 *  Solving this integral leads to
 *  <p>
 *  <img src="../doc-files/linear-precondition-y2.gif"
 *       alt="y_2 = -\frac{(x_2-x_1)\cdot y_1 - 2}{x_2-x_1}"
 *  >
 *  </p>
 *
 *  for fixed values for <i>x<sub>1</sub></i>, <i>x<sub>2</sub></i> and
 *  <i>y<sub>1</sub></i>.
 *  <p>
 *  If the value of <i>y<sub>2</sub></i> &lt; 0, the value of <i>x<sub>2</sub></i>
 *  is decreased so that the resulting triangle (<i>x<sub>1</sub></i>,0),
 *  (<i>x<sub>1</sub></i>,<i>y<sub>1</sub></i>), (<i>x<sub>2</sub></i>,0) has
 *  an area of <i>one</i>.
 *  </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class LinearDistribution implements Distribution {

	private final Interval _domain;

	private final double x1;
	private final double x2;
	private final double y1;
	private final double y2;
	private final double k;
	private final double d;

	public LinearDistribution(final Interval domain, final double y1) {
		_domain = requireNonNull(domain);

		this.y1 = Math.max(y1, 0.0);
		x1 = domain.min();
		y2 = Math.max(y2(x1, domain.max(), y1), 0.0);
		if (y2 == 0) {
			x2 = 2.0/ this.y1 + x1;
		} else {
			x2 = domain.max();
		}

		k = (y2 - this.y1)/(x2 - x1);
		d = this.y1 - k * x1;
	}

	private static double y2(final double x1, final double x2, final double y1) {
		return -((x2 - x1)*y1 - 2)/(x2 - x1);
	}

	@Override
	public Interval domain() {
		return _domain;
	}

	public double x1() {
		return x1;
	}

	public double x2() {
		return x2;
	}

	public double y1() {
		return y1;
	}

	public double y2() {
		return y2;
	}

	public double k() {
		return k;
	}

	public double d() {
		return d;
	}

	/**
	 * Return a new CDF object.
	 *
	 * <p>
	 * <img
	 *     src="../doc-files/linear-cdf.gif"
	 *     alt="f(x)=-\frac{(x^2-2x_2x)y_1 - (x^2 - 2x_1x)y_2}
	 *      {2(x_2 - x_1)}"
	 * >
	 * </p>
	 *
	 */
	@Override
	public Cdf cdf() {
		return x -> {
			double result;
			if (x < x1) {
				result = 0.0;
			} else if (x > x2) {
				result = 1.0;
			} else {
				result = k *x*x/2.0 + d *x;
			}

			return result;
		};
	}

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="../doc-files/linear-pdf.gif"
	 *     alt="f(x) = \left(
	 *                      \frac{y_2-y_1}{x_2-x_1} \cdot x +
	 *                      y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1
	 *                 \right)"
	 * >
	 * </p>
	 *
	 */
	@Override
	public Pdf pdf() {
		return x -> {
			double result = 0.0;
			if (x >= x1 && x <= x2) {
				result = k *x + d;
			}
			return result;
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(_domain, y1);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof LinearDistribution other &&
			Objects.equals(_domain, other._domain) &&
			Double.compare(y1, other.y1) == 0;
	}

	@Override
	public String toString() {
		return format(
			"LinearDistribution[(%f, %f), (%f, %f)]", x1, y1, x2, y2
		) ;
	}

}
