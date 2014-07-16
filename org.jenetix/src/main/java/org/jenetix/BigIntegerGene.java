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
package org.jenetix;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;

import org.jenetics.NumericGene;
import org.jenetics.util.Mean;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-16 $</em>
 */
public final class BigIntegerGene implements
	NumericGene<BigInteger, BigIntegerGene>,
	Mean<BigIntegerGene>
{
	private static final long serialVersionUID = 1L;

	private final BigInteger _value;
	private final BigInteger _min;
	private final BigInteger _max;

	private BigIntegerGene(
		final BigInteger value,
		final BigInteger min,
		final BigInteger max
	) {
		_value = requireNonNull(value);
		_min = requireNonNull(min);
		_max = requireNonNull(max);
	}

	@Override
	public BigInteger getAllele() {
		return _value;
	}

	@Override
	public BigInteger getMin() {
		return _min;
	}

	@Override
	public BigInteger getMax() {
		return _max;
	}

	@Override
	public BigIntegerGene mean(final BigIntegerGene that) {
		return null;
	}

	@Override
	public BigIntegerGene newInstance(final Number number) {
		return null;
	}

	@Override
	public BigIntegerGene newInstance(final BigInteger value) {
		return null;
	}

	@Override
	public BigIntegerGene newInstance() {
		return null;
	}

	public static BigIntegerGene of(
		final BigInteger value,
		final BigInteger min,
		final BigInteger max
	) {
		return new BigIntegerGene(value, min, max);
	}

	public static BigInteger of(final BigInteger min, final BigInteger max) {
		//final BigInteger value = new BigInteger()
		return null;
	}
}
