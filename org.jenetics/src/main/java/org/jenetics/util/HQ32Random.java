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
 * @version 1.1 &mdash; <em>$Date: 2012-11-16 $</em>
 */
public class HQ32Random extends Random {

	private static final long serialVersionUID = 1L;

	private int u;
	private int v = 0x85CA18E3;
	private int w1 = 0x1F123BB5;
	private int w2 = 0x159A55E5;

	public HQ32Random() {
		this((int)System.nanoTime());
	}

	public HQ32Random(int j) {
		u = j^v;
		nextInt();
		v = u;
		nextInt();
	}

	@Override
	public int nextInt() {
		u = u * 0xAC564B05 + 0x61C88639;
		v ^= v >> 13; v ^= v << 17; v ^= v >> 5;
		w1 = 33378 * (w1 & 0xffff) + (w1 >> 16);
		w2 = 57225 * (w2 & 0xffff) + (w2 >> 16);
		int x = u ^ (u << 9); x ^= x >> 17; x ^= x << 6;
		int y = w1 ^ (w1 << 17); y ^= y >> 15; y ^= y << 5;
		return (x + v) ^ (y + w2);
	}

	@Override
	public double nextDouble() {
		return 2.32830643653869629E-10*(
			nextInt() + 2.32830643653869629E-10*nextInt()
		);
	}

	public double nextFloat32() {
		return 2.32830643653869629E-10*nextInt();
	}


/*

	@Override
	public long nextLong() {
		u = u * 2862933555777941757L + 7046029254386353087L;
		v ^= v >>> 17;
		v ^= v << 31;
		v ^= v >>> 8;
		w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
		long x = u ^ (u << 21);
		x ^= x >>> 35;
		x ^= x << 4;
		long ret = (x + v) ^ w;
		return ret;
	}

	@Override
	protected int next(int bits) {
		return (int) (nextLong() >>> (64 - bits));
	}
	*/
}























