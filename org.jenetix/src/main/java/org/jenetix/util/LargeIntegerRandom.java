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
package org.jenetix.util;

import static java.util.Objects.requireNonNull;

import java.util.Random;

import javolution.context.StackContext;

import org.jscience.mathematics.number.LargeInteger;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.bit;
import org.jenetics.util.math;

/**
 * Random number generator for {@link LargeInteger} values within a defined
 * range.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-14 $</em>
 */
public class LargeIntegerRandom implements NumberRandom<LargeInteger> {

	private static final LargeInteger
	INT_MAX_VALUE = LargeInteger.valueOf(Integer.MAX_VALUE);

	private static final LargeInteger
	LONG_MAX_VALUE = LargeInteger.valueOf(Long.MAX_VALUE);

	private final Random _random;

	public LargeIntegerRandom(final Random random) {
		_random = requireNonNull(random, "Random");
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

		LargeInteger result = null;
		if (diff.compareTo(INT_MAX_VALUE) < 0) {
			result = LargeInteger.valueOf(random.nextInt(diff.intValue() + 1));
		} else if (diff.compareTo(LONG_MAX_VALUE) < 0) {
			result = LargeInteger.valueOf(
				math.random.nextLong(random, diff.longValue() + 1)
			);
		} else {
			result = next(random, diff);
		}

		return result.plus(min);
	}

	private static LargeInteger next(final Random random, final LargeInteger diff) {
		final int length = diff.isPowerOfTwo() ?
			diff.bitLength() - 1 :
			diff.bitLength();

		final byte[] bytes = new byte[(length >>> 3) + 1];

		StackContext.enter();
		try {
			LargeInteger result = LargeInteger.ONE;
			do {
				random.nextBytes(bytes);
				bit.shiftRight(bytes, (bytes.length << 3) - length);
				bit.reverse(bytes);

				result = LargeInteger.valueOf(bytes, 0, bytes.length);
			} while (result.isGreaterThan(diff));

			return StackContext.outerCopy(result);
		} finally {
			StackContext.exit();
		}
	}

}



