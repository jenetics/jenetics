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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-21 $</em>
 */
final class ModularArithmetic {

	private final long _modulus;

	ModularArithmetic(final long modulus) {
		_modulus  = modulus;
	}

	long add(final long a,final long b) {
		return (a%_modulus + b%_modulus)%_modulus;
	}

	long add(final long a,final long b, final long c) {
		return add(add(a, b), c);
	}

	long add(final long a,final long b, final long c, final long d) {
		return add(add(add(a, b), c), d);
	}

	long add(final long a,final long b, final long c, final long d, final long e) {
		return add(add(add(add(a, b), c), d), e);
	}

}
