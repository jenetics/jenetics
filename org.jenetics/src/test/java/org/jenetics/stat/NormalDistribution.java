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
import static org.jenetics.internal.math.statistics.Φ;
import static org.jenetics.internal.math.statistics.φ;
import static org.jenetics.internal.util.Equality.eq;
import static org.jenetics.internal.util.require.nonNegative;

import java.util.function.ToDoubleFunction;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Range;

/**
 * Normal (Gaussian) distribution. With
 *
 * <p>
 * <img
 *     src="doc-files/normal-pdf.gif"
 *     alt="f(x)=\frac{1}{\sqrt{2\pi \sigma^{2}}}\cdot
 *          e^{-\frac{(x-\mu)^2}{2\sigma^{2}}})"
 * >
 * </p>
 * as <i>pdf</i> and
 * <p>
 * <img
 *     src="doc-files/normal-cdf.gif"
 *     alt="f(x)=\frac{1}{2}\cdot \left [ 1 + \textup{erf} \left(
 *          \frac{x - \mu }{\sqrt{2\sigma^{2}}} \right) \right ]"
 * >
 * </p>
 * as <i>cdf</i>.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Normal_distribution">Normal distribution</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public class NormalDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N>
{

	private final Range<N> _domain;
	private final double _min;
	private final double _max;
	private final double _mean;
	private final double _var;
	private final double _stddev;

	/**
	 * Create a new normal distribution object.
	 *
	 * @param domain the domain of the distribution.
	 * @param mean the mean value of the normal distribution.
	 * @param var the variance of the normal distribution.
	 * @throws NullPointerException if the {@code domain} is {@code null}.
	 * @throws IllegalArgumentException if the variance is negative.
	 */
	public NormalDistribution(
		final Range<N> domain,
		final double mean,
		final double var
	) {
		_domain = requireNonNull(domain, "Domain");
		_min = domain.getMin().doubleValue();
		_max = domain.getMax().doubleValue();
		_mean = mean;
		_var = nonNegative(var, "Variance");
		_stddev = Math.sqrt(var);
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
	 *     src="doc-files/normal-cdf.gif"
	 *     alt="f(x)=\frac{1}{2}\cdot \left [ 1 + \textup{erf} \left(
	 *          \frac{x - \mu }{\sqrt{2\sigma^{2}}} \right) \right ]"
	 * >
	 * </p>
	 */
	@Override
	public ToDoubleFunction<N> getCDF() {
		return value -> {
			final double x = value.doubleValue();

			double result = 0.0;
			if (x < _min) {
				result = 0.0;
			} else if (x > _max) {
				result = 1.0;
			} else {
				result = Φ(x, _mean, _stddev);
			}

			return result;
		};
	}

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/normal-pdf.gif"
	 *     alt="f(x)=\frac{1}{\sqrt{2\pi \sigma^{2}}}\cdot e^{-\frac{(x-\mu)^2}{2\sigma^{2}}})"
	 * >
	 * </p>
	 */
	@Override
	public ToDoubleFunction<N> getPDF() {
		return value -> {
			final double x = value.doubleValue();

			double result = 0.0;
			if (_domain.contains(value)) {
				result = φ(x, _mean, _stddev);
			}

			return result;
		};
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_domain).and(_mean).and(_var).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(dist ->
			eq(_domain, dist._domain) &&
			eq(_mean, dist._mean) &&
			eq(_var, dist._var)
		);
	}

	@Override
	public String toString() {
		return format("N[µ=%f, σ²=%f]", _mean, _var);
	}

}
