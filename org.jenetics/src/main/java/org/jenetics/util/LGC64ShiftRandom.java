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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.Random;

/**
 * https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-11-25 $</em>
 */
public class LGC64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	public static final class Parameter {
		public final long a;
		public final long b;
		public Parameter(final long a, final long b) {
			this.a = a;
			this.b = b;
		}
	}

	public static final Parameter DEFAULT = new Parameter(0xFBD19FBBC5C07FF5L, 1L);

	private long _a = DEFAULT.a;
	private long _b = DEFAULT.b;
	private long _r = 0;

	public LGC64ShiftRandom() {
	}

	public LGC64ShiftRandom(final long seed) {
		_r = seed;
	}

	@Override
	public long nextLong() {
		step();

		long t = _r;
		t ^= t >>> 17;
		t ^= t << 31;
		t ^= t >>> 8;
		return t;
	}

	private void step() {
		_r = _a*_r + _b;
	}

	public void split(final int s, final int n) {
		if (s < 1 || n >= s) {
			throw new IllegalArgumentException();
		}

		if (s > 1) {
			jump(n + 1);
			_b *= f(s, _a);
			_a = math.pow(_a, s);
			backward();
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		}
	}

	public void jump2(final int s) {
		_r = _r*math.pow(_a, 1L << s) + f(1L << s, _a)*_b;
	}

	public void jump(final long step) {
		if (step < 16) {
			for (int i = 0; i < step; ++i) {
				step();
			}
		} else {
			long s = step;
			int i = 0;
			while (s > 0) {
				if (s%2 == 1) {
					jump2(i);
				}
				++i;
				s >>>= 1;
			}
		}
	}

	public void backward() {
		for (int i = 0; i < 64; ++i) {
			jump2(i);
		}
	}

	/**
	 * compute prod(1+a^(2^i), i=0..l-1)
	 */
	private static long g(final int l, final long a) {
		long p = a;
		long res = 1;
		for (int i = 0; i < l; ++i) {
			res *= 1 + p;
			p *= p;
		}

		return res;
	}

	/**
	 * compute sum(a^i, i=0..s-1)
	 */
	private static long f(final long s, final long a) {
		if (s == 0) {
			return 0;
		}

		long e = math.log2Floor(s);
		long y = 0;
		long p = a;

		for (int l = 0; l <= e; ++l) {
			if (((1L << l) & s) > 0) {
				y = g(l, a) + p*y;
			}
			p *= p;
		}

		return y;
	}


	@Override
	public void setSeed(final long seed) {
		_r = seed;
	}

	public static void main(final String[] args) {
		final Random random = new LGC64ShiftRandom();
		for (int i = 0; i < 15; ++i) {
			System.out.println(random.nextLong());
		}
	}

}






