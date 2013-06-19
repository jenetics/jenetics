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
package org.jenetics.util;

import java.util.Random;

/**
 * Base class for random generators which create 32 bit random values natively.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-19 $</em>
 */
public abstract class Random32 extends PRNG {

	private static final long serialVersionUID = 1L;

	protected Random32(long seed) {
		super(seed);
	}

	protected Random32() {
	}

	// Force to explicitly override the Random.nextInt() method.
	@Override
	public abstract int nextInt();


	@Override
	public long nextLong() {
		return ((long)(nextInt()) << 32) + nextInt();
	}

	@Override
	protected int next(final int bits) {
		return nextInt() >>> (32 - bits);
	}

	@Override
	public float nextFloat() {
		return math.random.toFloat2(nextInt());
	}

	/**
	 * Optimized version of the {@link Random#nextDouble()} method for 32-bit
	 * random engines.
	 */
	@Override
	public double nextDouble() {
		return math.random.toDouble2(nextInt(), nextInt());
	}

}






