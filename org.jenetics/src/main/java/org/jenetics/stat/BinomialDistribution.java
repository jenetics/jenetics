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

import static org.jenetics.util.object.checkProbability;
import static org.jenetics.util.object.nonNull;

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
		_domain = nonNull(domain, "Domain");
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
