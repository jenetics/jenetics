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
 * This implementation is not thread-safe.
 *
 * Numerical Recipes: Page 345, third edition, Chapter 7. Random Numbers
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public class XORShiftRandom extends Random {
	private static final long serialVersionUID = 1L;

	private long _x;

	public XORShiftRandom() {
		this(System.nanoTime());
	}

	public XORShiftRandom(final long seed) {
		_x = seed;
	}

	public static ThreadLocal<XORShiftRandom> newThreadLocal() {
		return new ThreadLocal<XORShiftRandom>() {
			@Override protected XORShiftRandom initialValue() {
				return new XORShiftRandom();
			}
		};
	}

	public static ThreadLocal<XORShiftRandom> newThreadLocal(final long seed) {
		return new ThreadLocal<XORShiftRandom>() {
			@Override protected XORShiftRandom initialValue() {
				return new XORShiftRandom(seed);
			}
		};
	}

	@Override
	public int nextInt() {
		//final long x = nextLong();
		//return (int)(x >>> 32)^(int)(x << 32);
		return (int)(nextLong() >>> 32);
	}

	/**
	 *  21, 35, 4
	 * 20, 41, 5
	 * 17, 31, 8
	 * 11, 29, 14
	 * 14, 29, 11
	 * 30, 35, 13
	 * 21, 37, 4
	 * 21, 43, 4
	 * 23, 41, 18
	 *
	 */
	@Override
	public long nextLong() {
		_x ^= (_x << 21);
		_x ^= (_x >>> 35);
		_x ^= (_x << 4);
		return _x;
	}

	@Override
	public float nextFloat() {
		return (float)(2.32830643653869629E-10*nextInt());
	}

	@Override
	public double nextDouble() {
		return 5.42101086242752217E-20*nextLong();
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}
}







