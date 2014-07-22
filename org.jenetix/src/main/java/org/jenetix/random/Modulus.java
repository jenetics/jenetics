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
package org.jenetix.random;

import static java.lang.String.format;

import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date$</em>
 */
final class Modulus extends StaticObject {
	private Modulus() {}

	static final long VALUE = 0xFFFFFFFFL;

	private static final long MASK = (1L << 32) - 1L;


	static long mod(final long a) {
		return a%VALUE;
	}


//	static long mod(final long x) {
//		return x >= 0 ? modp(x) : -modp(-x);
//	}

	private static long modp(final long x) {
		return x%VALUE;
//		assert(x >= 0): format("%d not positive.", x);
//
//		long k = x;
//		while (k > MASK) k = (k&MASK) + (k >>> 32);
//		return k == MASK ? 0 : k;
	}

	static long add(final long a,final long b) {
		final long r = a + b;
		if (((a^r) & (b^r)) < 0) return modp(modp(a) + modp(b));
		else return modp(r);
	}

	static long add(final long a,final long b, final long c) {
		return add(add(a, b), c);
	}

	static long add(final long a,final long b, final long c, final long d) {
		return modp(add(a, b) + add(c, d));
	}

	static long add(final long a,final long b, final long c, final long d, final long e) {
		return modp(add(a, b, c, d) + modp(e));
	}

}
