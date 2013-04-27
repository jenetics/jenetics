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

import static java.lang.Double.NaN;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;


/**
 * <p>Calculate the variance from a finite sample of <i>N</i> observations.</p>
 * <p><img src="doc-files/variance.gif"
 *         alt="s^2_{N-1}=\frac{1}{N-1}\sum_{i=1}^{N}\left ( x_i - \bar{x} \right )^2"
 *    />
 * </p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance" >
 *       Wikipedia: Algorithms for calculating variance</a>
 * @see <a href="http://mathworld.wolfram.com/Variance.html">
 *       Wolfram MathWorld: Variance</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-04-27 $</em>
 */
public class Variance<N extends Number> extends Mean<N> {

	private double _m2 = NaN;

	public Variance() {
	}

	/**
	 * Return the variance of the accumulated values.
	 * <p><img src="doc-files/variance.gif" alt="Variance" /></p>
	 *
	 * @return the variance of the accumulated values, or {@link java.lang.Double#NaN}
	 *          if {@code getSamples() == 0}.
	 */
	public double getVariance() {
		double variance = NaN;

		if (_samples == 1) {
			variance = _m2;
		} else if (_samples > 1) {
			variance = _m2/(_samples - 1);
		}

		return variance;
	}

	/**
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	@Override
	public void accumulate(final N value) {
		if (_samples == 0) {
			_mean = 0;
			_m2 = 0;
		}

		final double data = value.doubleValue();
		final double delta = data - _mean;

		_mean += delta/(++_samples);
		_m2 += delta*(data - _mean);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).and(_m2).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final Variance<?> variance = (Variance<?>)obj;
		return eq(_m2, variance._m2) && super.equals(variance);
	}

	@Override
	public String toString() {
		return String.format(
			"%s[samples=%d, mean=%f, stderr=%f, var=%f]",
			getClass().getSimpleName(),
			getSamples(),
			getMean(),
			getStandardError(),
			getVariance()
		);
	}

	@Override
	public Variance<N> clone() {
		return (Variance<N>)super.clone();
	}

}

