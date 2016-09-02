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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
class IntMath {
	private IntMath() {}

	/**
	 * Binary exponentiation algorithm.
	 *
	 * @param b the base number.
	 * @param e the exponent.
	 * @return {@code b^e}.
	 */
	static long pow(final long b, final long e) {
		long base = b;
		long exp = e;
		long result = 1;

		while (exp != 0) {
			if ((exp & 1) != 0) {
				result *= base;
			}
			exp >>>= 1;
			base *= base;
		}

		return result;
	}

	static long log2Floor(final long x) {
		return Long.SIZE - 1 - Long.numberOfLeadingZeros(x);
	}

	static long log2Ceil(final long x) {
		return Long.SIZE - Long.numberOfLeadingZeros(x - 1);
	}

}
