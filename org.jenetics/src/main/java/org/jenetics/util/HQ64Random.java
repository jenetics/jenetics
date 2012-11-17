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
 *Implementation of the highest quality recommended generator. The constructor is called with
an integer seed and creates an instance of the generator. The member functions int64, doub,
and int32 return the next values in the random sequence, as a variable type indicated by their
names. The period of the generator is
 3.138 * 10^57.

 Seite 342
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public class HQ64Random extends Random {

	private static final long serialVersionUID = 1L;

	private long u = 0L;
	private long v = 0L;
	private long w = 0L;

	public HQ64Random() {
		this(System.nanoTime());
	}

	public HQ64Random(final long seed) {
		init(seed);
	}

	private void init(final long seed) {
		final long s = seed == 0 ? 0xdeadbeef : seed;

		u = s^v;
		v = 4101842887655102017L;
		w  = 1L;
		nextLong();
		v = u;
		nextLong();
		w = v;
		nextLong();
	}

	@Override
	public int nextInt() {
		//final long x = nextLong();
		//return (int)(x >>> 32)^(int)(x << 32);
		return (int)(nextLong() >>> 32);
	}

	@Override
	public long nextLong() {
		u = u*2862933555777941757L + 7046029254386353087L;
		v ^= v >> 17;
		v ^= v << 31;
		v ^= v >> 8;
		w = 0xFFFFDA61*(w & 0xFFFFFFFF) + (w >> 32);

		long x = u^(u << 21);
		x ^= x >> 35;
		x ^= x << 4;

		return (x + v) ^ w;
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
	protected int next(int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}

	@Override
	public void setSeed(final long seed) {
		init(seed);
	}

}
