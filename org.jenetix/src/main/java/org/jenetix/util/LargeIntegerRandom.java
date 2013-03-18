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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
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
package org.jenetix.util;

import java.util.Random;

import org.jscience.mathematics.number.LargeInteger;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.bit;
import org.jenetics.util.object;

/**
 * Random number generator for {@link LargeInteger} values within a defined range.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-03-18 $</em>
 */
public class LargeIntegerRandom implements NumberRandom<LargeInteger> {

	private static final LargeInteger
	INT_MAX_VALUE = LargeInteger.valueOf(Integer.MAX_VALUE);

	private final Random _random;

	public LargeIntegerRandom(final Random random) {
		_random = object.nonNull(random, "Random");
	}

	public LargeIntegerRandom() {
		this(RandomRegistry.getRandom());
	}

	@Override
	public LargeInteger next(final LargeInteger min, final LargeInteger max) {
		return next(_random, min, max);
	}

	public static LargeInteger next(
		final Random random,
		final LargeInteger min,
		final LargeInteger max
	) {
		if (min.isGreaterThan(max)) {
			throw new IllegalArgumentException(String.format(
				"Illegal range: %s > %s.", min, max
			));
		}

		final LargeInteger diff = max.minus(min);
		assert (!diff.isNegative());

		final LargeInteger result = diff.compareTo(INT_MAX_VALUE) <= 0 ?
			LargeInteger.valueOf(random.nextInt(diff.intValue())) :
			next(random, diff);

		return result.plus(min);
	}

	private static LargeInteger next(final Random random, final LargeInteger diff) {
		final int length = diff.isPowerOfTwo() ?
			diff.bitLength() - 1 :
			diff.bitLength();

		final byte[] bytes = new byte[(length >>> 3) + 1];

		LargeInteger result = null;
		do {
			random.nextBytes(bytes);
			bit.shiftRight(bytes, (bytes.length << 3) - length);
			bit.reverse(bytes);

			result = LargeInteger.valueOf(bytes, 0, bytes.length);
		} while (result.isGreaterThan(diff));

		return result;
	}

}



