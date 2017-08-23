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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.util.function.ToDoubleFunction;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Range;


/**
 * <p>This distribution has the following cdf.</p>
 * <p><img src="doc-files/LinearDistribution.png" alt="Distribution"></p>
 * <p>
 * The only restriction is that the integral of the cdf must be one.
 * </p>
 * <p>
 * <img src="doc-files/linear-precondition.gif"
 *      alt="\int_{x_1}^{x_2}\left(
 *             \\underset{k} {\\underbrace {\frac{y_2-y_1}{x_2-x_1}}} \cdot x +
 *             \\underset{d}{\\underbrace {y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1}}
 *           \right)\mathrm{d}x = 1"
 *  >
 *  </p>
 *
 *  Solving this integral leads to
 *  <p>
 *  <img src="doc-files/linear-precondition-y2.gif"
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LinearDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N>
{

	private final Range<N> _domain;

	private final double _x1;
	private final double _x2;
	private final double _y1;
	private final double _y2;
	private final double _k;
	private final double _d;

	public LinearDistribution(final Range<N> domain, final double y1) {
		_domain = requireNonNull(domain);

		_y1 = Math.max(y1, 0.0);
		_x1 = domain.getMin().doubleValue();
		_y2 = Math.max(y2(_x1, domain.getMax().doubleValue(), y1), 0.0);
		if (_y2 == 0) {
			_x2 = 2.0/_y1 + _x1;
		} else {
			_x2 = domain.getMax().doubleValue();
		}

		_k = (_y2 - _y1)/(_x2 - _x1);
		_d = _y1 - _k*_x1;
	}

	private static double y2(final double x1, final double x2, final double y1) {
		return -((x2 - x1)*y1 - 2)/(x2 - x1);
	}

	@Override
	public Range<N> getDomain() {
		return _domain;
	}

	/**
	 * Return a new CDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/linear-cdf.gif"
	 *     alt="f(x)=-\frac{(x^2-2x_2x)y_1 - (x^2 - 2x_1x)y_2}
	 *      {2(x_2 - x_1)}"
	 * >
	 * </p>
	 *
	 */
	@Override
	public ToDoubleFunction<N> getCDF() {
		return value -> {
			final double x = value.doubleValue();

			double result = 0;
			if (x < _x1) {
				result = 0.0;
			} else if (x > _x2) {
				result = 1.0;
			} else {
				result = _k*x*x/2.0 + _d*x;
			}

			return result;
		};
	}

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/linear-pdf.gif"
	 *     alt="f(x) = \left(
	 *                      \frac{y_2-y_1}{x_2-x_1} \cdot x +
	 *                      y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1
	 *                 \right)"
	 * >
	 * </p>
	 *
	 */
	@Override
	public ToDoubleFunction<N> getPDF() {
		return value -> {
			final double x = value.doubleValue();

			double result = 0.0;
			if (x >= _x1 && x <= _x2) {
				result = _k*x + _d;
			}
			return result;
		};
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).
				and(_domain).
				and(_x1).and(_x2).
				and(_y1).and(_y2).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(dist ->
			eq(_domain, dist._domain) &&
			eq(_x1, dist._x1) && eq(_x2, dist._x2) &&
			eq(_y1, dist._y1) && eq(_y2, dist._y2)
		);
	}

	@Override
	public String toString() {
		return format(
			"LinearDistribution[(%f, %f), (%f, %f)]",
			_x1, _y1, _x2, _y2
		) ;
	}

}
