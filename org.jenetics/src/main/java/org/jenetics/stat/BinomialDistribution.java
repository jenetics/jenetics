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
package org.jenetics.stat;

import static java.util.Objects.requireNonNull;
import static org.jenetics.util.object.checkProbability;

import java.io.Serializable;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Function;
import org.jenetics.util.Range;

/**
 * TODO: implement BinomialDistribution
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
class BinomialDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N>
{

	static final class PDF<N extends Number & Comparable<? super N>>
		implements
			Function<N, Float64>,
			Serializable
	{
		private static final long serialVersionUID = 1L;

		private final Range<N> _domain;

		private final long _N;
		private final double _p;
		private final double _q;

		public PDF(final Range<N> domain, final double p) {
			_domain = domain;
			_N = domain.getMax().longValue() - domain.getMin().longValue();
			_p = p;
			_q = 1.0 - p;
		}

		@Override
		public Float64 apply(final N value) {
			final long x = value.longValue() - _domain.getMin().longValue();

			Float64 result = Float64.ZERO;
			if (_domain.contains(value)) {
				result = Float64.valueOf(
						binomial(_N, x)*Math.pow(_p, x)*Math.pow(_q, _N - x)
					);
			}

			return result;
		}

		@Override
		public String toString() {
			return String.format("p(x) = %s", "");
		}

	}

	static final class CDF<N extends Number & Comparable<? super N>>
		implements
			Function<N, Float64>,
			Serializable
	{
		private static final long serialVersionUID = 1L;

		private final Range<N> _domain;

		private final long _N;
		private final double _p;
		private final double _q;

		public CDF(final Range<N> domain, final double p) {
			_domain = domain;
			_N = domain.getMax().longValue() - domain.getMin().longValue();
			_p = p;
			_q = 1.0 - p;
		}

		@Override
		public Float64 apply(final N value) {
			long x = value.longValue();

			Float64 result = null;
			if (_domain.getMin().longValue() > x) {
				result = Float64.ZERO;
			} else if (_domain.getMax().longValue() < x) {
				result = Float64.ONE;
			} else {
				x = x - _domain.getMin().longValue();
				double v = 0;
				for (long i = 0; i <= x; ++i) {
					v += binomial(_N, i)*Math.pow(_p, i)*Math.pow(_q, _N - i);
				}
				result = Float64.valueOf(v);
			}

			return result;
		}

		@Override
		public String toString() {
			return String.format("p(x) = %s", "");
		}

	}

	private final Range<N> _domain;
	private final double _p;

	public BinomialDistribution(final Range<N> domain, final double p) {
		_domain = requireNonNull(domain, "Domain");
		_p = checkProbability(p);
	}

	@Override
	public Range<N> getDomain() {
		return _domain;
	}

	@Override
	public Function<N, Float64> getCDF() {
		return new CDF<>(_domain, _p);
	}

	@Override
	public Function<N, Float64> getPDF() {
		return new PDF<>(_domain, _p);
	}

	private static double binomial(final long n, final long k) {
		long b = 1;
		for (long i = 1; i <= k; ++i) {
			b *= (n - k + i)/i;
		}
		return b;
	}

}
