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
 * High-quality random generator using only 32-bit arithmetic. Same
 * conventions as Ran. Period ~3.11 * 10^37. Recommended only when 64-bit
 * arithmetic is not available.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-11-17 $</em>
 */
public class HQ32Random extends Random {

	private static final long serialVersionUID = 1L;

	private int u = 0;
	private int v = 0;
	private int w1 = 0;
	private int w2 = 0;

	public HQ32Random() {
		this(System.nanoTime());
	}

	public HQ32Random(long seed) {
		init(seed);
	}

	private void init(final long seed) {
		final long s = seed == 0 ? 0xdeadbeef : seed;

		v = 0x85CA18E3;
		w1 = 0x1F123BB5;
		w2 = 0x159A55E5;

		u = ((int)(s >>> 32)^(int)(s << 32))^v;
		nextInt();
		v = u;
		nextInt();
	}

	@Override
	public int nextInt() {
		u = u*0xAC564B05 + 0x61C88639;
		v ^= v >> 13;
		v ^= v << 17;
		v ^= v >> 5;
		w1 = 33378*(w1 & 0xffff) + (w1 >> 16);
		w2 = 57225*(w2 & 0xffff) + (w2 >> 16);
		int x = u^(u << 9);
		x ^= x >> 17;
		x ^= x << 6;
		int y = w1^(w1 << 17);
		y ^= y >> 15;
		y ^= y << 5;
		return (x + v) ^ (y + w2);
	}

	@Override
	public long nextLong() {
		return ((long)nextInt() << 32) | ((long)nextInt() >>> 32);
	}

	@Override
	public double nextDouble() {
		return 2.32830643653869629E-10*(
			nextInt() + 2.32830643653869629E-10*nextInt()
		);
	}

	@Override
	public float nextFloat() {
		return (float)(2.32830643653869629E-10*nextInt());
	}

	@Override
	protected int next(final int bits) {
		return nextInt() >>> (32 - bits);
	}

	@Override
	public void setSeed(final long seed) {
		init(seed);
	}

}



