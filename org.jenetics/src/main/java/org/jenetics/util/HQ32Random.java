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
 * @version 1.1 &mdash; <em>$Date: 2012-11-18 $</em>
 */
public class HQ32Random extends Random {

	private static final long serialVersionUID = 1L;

	private int _u = 0;
	private int _v = 0;
	private int _w1 = 0;
	private int _w2 = 0;

	public HQ32Random() {
		this(System.nanoTime());
	}

	public HQ32Random(long seed) {
		init(seed);
	}

	private void init(final long seed) {
		final long s = seed == 0 ? 0xdeadbeef : seed;

		_v = 0x85CA18E3;
		_w1 = 0x1F123BB5;
		_w2 = 0x159A55E5;

		_u = ((int)(s >>> 32)^(int)(s << 32))^_v;
		nextInt();
		_v = _u;
		nextInt();
	}

	@Override
	public int nextInt() {
		_u = _u*0xAC564B05 + 0x61C88639;
		_v ^= _v >> 13;
		_v ^= _v << 17;
		_v ^= _v >> 5;
		_w1 = 33378*(_w1 & 0xffff) + (_w1 >> 16);
		_w2 = 57225*(_w2 & 0xffff) + (_w2 >> 16);
		int x = _u^(_u << 9);
		x ^= x >> 17;
		x ^= x << 6;
		int y = _w1^(_w1 << 17);
		y ^= y >> 15;
		y ^= y << 5;
		return (x + _v) ^ (y + _w2);
	}

	@Override
	public long nextLong() {
		return ((long)nextInt() << 32) | ((long)nextInt() >>> 32);
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



