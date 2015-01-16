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
 * @since !__version__!
 * @version !__version__!
 */
final class OxFFFFFFFB {
	private OxFFFFFFFB() {}

	// Largest prime number smaller than 2^32.
	static final long VALUE = 0xFFFFFFFBL;

	static long mod(final long a) {
		return a%VALUE;
	}

	static long add(final long a,final long b) {
		return mod(mod(a) + mod(b));
	}

	static long add(final long a,final long b, final long c) {
		return mod(add(a, b) + mod(c));
	}

	static long add(final long a,final long b, final long c, final long d) {
		return mod(add(a, b) + add(c, d));
	}

	static long add(final long a,final long b, final long c, final long d, final long e) {
		return mod(add(a, b, c, d) + mod(e));
	}

}
