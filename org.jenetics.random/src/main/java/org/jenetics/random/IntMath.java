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
package org.jenetics.random;

import java.math.RoundingMode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
class IntMath {
	private IntMath() {}

	/*
	template<long m>
	struct log2_floor {
		enum { value = 1 + log2_floor<m/2>::value };
	};

	template<>
	struct log2_floor<0> { enum { value = 0 }; };

	template<>
	struct log2_floor<1> { enum { value = 0 }; };

	template<long m>
	struct log2_ceil {
		enum { value = (1ul<<log2_floor<m>::value)<m ?
			log2_floor<m>::value+1 : log2_floor<m>::value };
	};
	*/

	static long log2Floor(final long x) {
		return Long.SIZE - 1 - Long.numberOfLeadingZeros(x);
	}

	static long log2Ceil(final long x) {
		return Long.SIZE - Long.numberOfLeadingZeros(x - 1);
	}


	public static void main(final String[] args) {
		for (int i = 0; i < 10000; ++i) {
			final long value = Long.SIZE - 1 - Long.numberOfLeadingZeros(i);
			System.out.println("" + i + ": " + log2Floor(i) + "==" + value);
			assert value == log2Floor(i);
		}
	}

/*
	public static int log2(long x, RoundingMode mode) {
		switch (mode) {
			case UNNECESSARY:
				checkRoundingUnnecessary(isPowerOfTwo(x));
				// fall through
			case DOWN:
			case FLOOR:
				return (Long.SIZE - 1) - Long.numberOfLeadingZeros(x);

			case UP:
			case CEILING:
				return Long.SIZE - Long.numberOfLeadingZeros(x - 1);

			case HALF_DOWN:
			case HALF_UP:
			case HALF_EVEN:
				// Since sqrt(2) is irrational, log2(x) - logFloor cannot be exactly 0.5
				int leadingZeros = Long.numberOfLeadingZeros(x);
				long cmp = MAX_POWER_OF_SQRT2_UNSIGNED >>> leadingZeros;
				// floor(2^(logFloor + 0.5))
				int logFloor = (Long.SIZE - 1) - leadingZeros;
				return logFloor + lessThanBranchFree(cmp, x);

			default:
				throw new AssertionError("impossible");
		}
	}
	*/

}
