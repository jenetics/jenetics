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
 * This is the implementation of the <i>highest quality recommended generator,</i>
 * suggested in
 * <p>
 * <strong>Numerical Recipes 3rd Edition: The Art of Scientific Computing</strong>
 * <br/>
 * <em>Chapter 7. Random Numbers; Page 342</em>
 * <br/>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br/>
 * ISBN:0521880688 9780521880688
 * <br/>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 * <p/>
 * The period of the generator is &asymp;3.138&sdot;10<sup>57</sup>. This
 * implementation is not thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public class HQ64Random extends Random {

	private static final long serialVersionUID = 1L;

	private long _u = 0L;
	private long _v = 0L;
	private long _w = 0L;


	public HQ64Random() {
		this(System.nanoTime());
	}

	public HQ64Random(final long seed) {
		init(seed);
	}

	private void init(final long seed) {
		final long s = seed == 0 ? 0xdeadbeef : seed;

		_u = s^_v;
		_v = 4101842887655102017L;
		_w  = 1L;
		nextLong();
		_v = _u;
		nextLong();
		_w = _v;
		nextLong();
	}

	@Override
	public long nextLong() {
		_u = _u*2862933555777941757L + 7046029254386353087L;
		_v ^= _v >> 17;
		_v ^= _v << 31;
		_v ^= _v >> 8;
		_w = 0xFFFFDA61*(_w & 0xFFFFFFFF) + (_w >> 32);

		long x = _u^(_u << 21);
		x ^= x >> 35;
		x ^= x << 4;

		return (x + _v) ^ _w;
	}

//	@Override
//	public float nextFloat() {
//		return (float)(2.32830643653869629E-10*nextInt());
//	}

//	@Override
//	public double nextDouble() {
//		return 5.42101086242752217E-20*nextLong();
//	}

	@Override
	protected int next(int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}

	@Override
	public void setSeed(final long seed) {
		init(seed);
	}

}
