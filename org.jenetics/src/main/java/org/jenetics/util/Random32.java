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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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
package org.jenetics.util;

import java.util.Random;

/**
 * Base class for random generators which create 32 bit random values natively.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1 xxx
 * @version 1.2 xxx &mdash; <em>$Date: 2013-03-28 $</em>
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






