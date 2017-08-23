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
 * <a href="http://en.wikipedia.org/wiki/Uniform_distribution_%28continuous%29">
 * Uniform distribution</a> class.
 *
 * @see LinearDistribution
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class UniformDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N>
{

	private final Range<N> _domain;
	private final double _min;
	private final double _max;

	/**
	 * Create a new uniform distribution with the given {@code domain}.
	 *
	 * @param domain the domain of the distribution.
	 * @throws NullPointerException if the {@code domain} is {@code null}.
	 */
	public UniformDistribution(final Range<N> domain) {
		_domain = requireNonNull(domain, "Domain");
		_min = _domain.getMin().doubleValue();
		_max = _domain.getMax().doubleValue();
	}

	/**
	 * Create a new uniform distribution with the given min and max values.
	 *
	 * @param min the minimum value of the domain.
	 * @param max the maximum value of the domain.
	 * @throws IllegalArgumentException if {@code min >= max}
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public UniformDistribution(final N min, final N max) {
		this(new Range<>(min, max));
	}

	@Override
	public Range<N> getDomain() {
		return _domain;
	}

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/uniform-pdf.gif"
	 *     alt="f(x)=\left\{\begin{matrix}
	 *          \frac{1}{max-min} & for & x \in [min, max] \\
	 *          0 & & otherwise \\
	 *          \end{matrix}\right."
	 * >
	 * </p>
	 *
	 */
	@Override
	public ToDoubleFunction<N> getPDF() {
		return value -> {
			final double x = value.doubleValue();
			return (x >= _min && x <= _max) ? 1.0/(_max - _min) : 0.0;
		};
	}

	/**
	 * Return a new CDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/uniform-cdf.gif"
	 *     alt="f(x)=\left\{\begin{matrix}
	 *         0 & for & x < min \\
	 *         \frac{x-min}{max-min} & for & x \in [min, max] \\
	 *         1 & for & x > max  \\
	 *         \end{matrix}\right."
	 * >
	 * </p>
	 *
	 */
	@Override
	public ToDoubleFunction<N> getCDF() {
		return value -> {
			final double x = value.doubleValue();
			final double divisor = _max - _min;

			double result = 0.0;
			if (x < _min) {
				result = 0.0;
			} else if (x > _max) {
				result = 1.0;
			} else {
				result = (x - _min)/divisor;
			}

			return result;
		};
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_domain).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(dist -> eq(_domain, dist._domain));
	}

	@Override
	public String toString() {
		return format("UniformDistribution[%s]", _domain);
	}

}
