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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-24 $</em>
 * @since 3.0
 */
public final class DoubleAdder
	extends Number
	implements Comparable<DoubleAdder>
{
	private static final long serialVersionUID = 1L;

	private double _sum = 0.0;
	private double _simpleSum = 0.0;
	private double _compensation = 0.0;

	public DoubleAdder(final double value) {
		add(value);
	}

	public DoubleAdder() {
	}

	private DoubleAdder reset() {
		_sum = 0.0;
		_simpleSum = 0.0;
		_compensation = 0.0;
		return this;
	}

	public DoubleAdder set(final double value) {
		return reset().add(value);
	}

	public DoubleAdder set(final DoubleAdder value) {
		return reset().add(value);
	}

	public DoubleAdder add(final double value) {
		addWithCompensation(value);
		_simpleSum += value;
		return this;
	}

	private void addWithCompensation(final double value) {
		final double y = value - _compensation;
		final double t = _sum + y;
		_compensation = (t - _sum) - y;
		_sum = t;
	}

	public DoubleAdder add(final DoubleAdder value) {
		addWithCompensation(value._sum);
		addWithCompensation(value._compensation);
		_simpleSum += value._simpleSum;
		return this;
	}

	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	@Override
	public long longValue() {
		return (long)doubleValue();
	}

	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	@Override
	public double doubleValue() {
		// Better error bounds to add both terms as the final sum
		double result =  _sum + _compensation;
		if (Double.isNaN(result) && Double.isInfinite(_simpleSum)) {
			// If the compensated sum is spuriously NaN from
			// accumulating one or more same-signed infinite values,
			// return the correctly-signed infinity stored in
			// simpleSum.
			result = _simpleSum;
		}

		return result;
	}

	@Override
	public int compareTo(final DoubleAdder other) {
		return Double.compare(doubleValue(), other.doubleValue());
	}

	@Override
	public String toString() {
		return Double.toString(doubleValue());
	}

}
