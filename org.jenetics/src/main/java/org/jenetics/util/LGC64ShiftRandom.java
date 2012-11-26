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


/**
 * Linear congruential generators with modulus 2<sup>64</sup> with additional
 * bit-shift transformation.
 * <p/>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp">
 * trng::lcg64_shift</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library and produces exactly
 * the same sequence of PRNs.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public class LGC64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	public static final class Parameter {
		final long a;
		final long b;
		Parameter(final long a, final long b) {
			this.a = a;
			this.b = b;
		}
	}

	public static final Parameter DEFAULT = new Parameter(0xFBD19FBBC5C07FF5L, 1L);
	public static final Parameter LEcuyer1 = new Parameter(0x27BB2EE687B0B0FDL, 1L);
	public static final Parameter LEcuyer2 = new Parameter(0x2C6FE96EE78B6955L, 1L);
	public static final Parameter LEcuyer3 = new Parameter(0x369DEA0F31A53F85L, 1L);

	public static final ThreadLocal<LGC64ShiftRandom>
	INSTANCE = new ThreadLocal<LGC64ShiftRandom>() {
		@Override
		protected LGC64ShiftRandom initialValue() {
			return null;
		}
	};

	private long _a = DEFAULT.a;
	private long _b = DEFAULT.b;
	private long _r = 0;

	public LGC64ShiftRandom() {
		this(0);
	}

	public LGC64ShiftRandom(final long seed) {
		this(seed, DEFAULT);
	}

	public LGC64ShiftRandom(final long seed, final Parameter parameter) {
		_r = seed;
		_a = parameter.a;
		_b = parameter.b;
	}

	@Override
	public void setSeed(final long seed) {
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

	/**
	 * Changes the internal state of the PRNG in a way that future calls to
	 * {@link #nextLong()} will generated the s<sup>th</sup> sub-stream of
	 * p<sup>th</sup> sub-streams. <i>s</i> must be within the range of
	 * {@code [0, n)}.
	 *
	 * @param p the overall number of sub-streams
	 * @param s the s<sup>th</sup> sub-stream
	 * @throws IllegalArgumentException if {@code p < 1 || s >= p}.
	 */
	public void split(final int p, final int s) {
		if (p < 1) {
			throw new IllegalArgumentException(String.format(
				"p must be >= 1 but was %d.", p
			));
		}
		if (s >= p) {
			throw new IllegalArgumentException(String.format(
				"s must be < %d but was %d.", p, s
			));
		}

		if (p > 1) {
			jump(s + 1);
			_b *= f(p, _a);
			_a = pow(_a, p);
			backward();
		}
	}

	/**
	 * Changes the internal state of the PRNG in such a way that the engine
	 * <i>jumps</i> 2<sup>s</sup> steps ahead.
	 *
	 * @param s the 2<sup>s</sup> steps to jump ahead.
	 * @throws IllegalArgumentException if {@code s < 0}.
	 */
	public void jump2(final int s) {
		if (s < 0) {
			throw new IllegalArgumentException(String.format(
				"s must be positive but was %d.", s
			));
		}

		if (s >= Long.SIZE) {
			throw new IllegalArgumentException(String.format(
				"The 'jump2' size must be smaller than %d but was %d.",
				Long.SIZE, s
			));
		}

		_r = _r*pow(_a, 1L << s) + f(1L << s, _a)*_b;
	}

	/**
	 * Changes the internal state of the PRNG in such a way that the engine
	 * <i>jumps</i> s steps ahead.
	 *
	 * @param s the steps to jump ahead.
	 * @throws IllegalArgumentException if {@code s < 0}.
	 */
	public void jump(final long step) {
		if (step < 0) {
			throw new IllegalArgumentException(String.format(
				"step must be positive but was %d", step
			));
		}

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
				s >>= 1;
			}
		}
	}

	private void backward() {
		for (int i = 0; i < Long.SIZE; ++i) {
			jump2(i);
		}
	}

	/**
	 * Compute prod(1+a^(2^i), i=0..l-1).
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
	 * Compute sum(a^i, i=0..s-1).
	 */
	private static long f(final long s, final long a) {
		long y = 0;

		if (s != 0) {
			long e = log2Floor(s);
			long p = a;

			for (int l = 0; l <= e; ++l) {
				if (((1L << l) & s) != 0) {
					y = g(l, a) + p*y;
				}
				p *= p;
			}
		}

		return y;
	}

	private static long pow(final long b, final long e) {
		long base = b;
		long exp = e;
		long result = 1;

		while (exp != 0) {
			if ((exp & 1) != 0) {
				result *= base;
			}
			base *= base;
			exp >>>= 1;
		}

		return result;
	}

	private static long log2Floor(final long s) {
		long x = s;
		long y = 0;

		while (x != 0) {
			x >>>= 1;
			++y;
		}

		return y - 1;
	}

}






