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

import static org.jenetics.util.object.hashCodeOf;

import java.io.Serializable;


/**
 * This class implements a linear congruental PRNG with additional bit-shift
 * transition. The base recursion
 * <p><div align="center">
 * <img
 *     alt="r_{i+1} = (a\cdot r_i + b) \mod 2^{64}"
 *     src="doc-files/lcg-recursion.gif"
 * />
 * </p></div>
 * is followed by a non-linear transformation
 * <p><div align="center">
 * <img
 *     alt="\begin{eqnarray*}
 *           t &=& r_i                \\
 *           t &=& t \oplus (t >> 17) \\
 *           t &=& t \oplus (t << 31) \\
 *           t &=& t \oplus (t >> 8)
 *         \end{eqnarray*}"
 *     src="doc-files/lcg-non-linear.gif"
 * />
 * </p></div>
 * which destroys the lattice structure introduced by the recursion. The period of
 * this PRNG is 2<sup>64</sup>, {@code iff} <i>b</i> is odd and <i>a</i>
 * {@code mod} 4 = 1.
 * <p/>
 *
 * <p><b>
 * The <i>main</i> class of this PRNG is not thread safe. To create an thread
 * safe instances of this PRNG, use the {@link LCG64ShiftRandom.ThreadSafe} or
 * {@link LCG64ShiftRandom.ThreadLocal} class.
 * </b></p>
 *
 * <em>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp">
 * trng::lcg64_shift</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library created by Heiko
 * Bauke.</em>
 *
 * @see <a href="http://numbercrunch.de/trng/">TRNG</a>
 * @see RandomRegistry
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-12-12 $</em>
 */
public class LCG64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	/**
	 * Parameter class for the {@code LCG64ShiftRandom} generator, for the
	 * parameters <i>a</i> and <i>b</i> of the LC recursion
	 * <i>r<sub>i+1</sub> = a · r<sub>i</sub> + b</i> mod <i>2<sup>64</sup></i>.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2012-12-12 $</em>
	 */
	public static final class Param implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * The default PRNG parameters: a = 0xFBD19FBBC5C07FF5L; b = 1
		 */
		public static final Param DEFAULT = new Param(0xFBD19FBBC5C07FF5L, 1L);

		/**
		 * LEcuyer 1 parameters: a = 0x27BB2EE687B0B0FDL; b = 1
		 */
		public static final Param LECUYER1 = new Param(0x27BB2EE687B0B0FDL, 1L);

		/**
		 * LEcuyer 2 parameters: a = 0x369DEA0F31A53F85L; b = 1
		 */
		public static final Param LECUYER2 = new Param(0x2C6FE96EE78B6955L, 1L);

		/**
		 * LEcuyer 3 parameters: a = 0x369DEA0F31A53F85L; b = 1
		 */
		public static final Param LECUYER3 = new Param(0x369DEA0F31A53F85L, 1L);


		/**
		 * The parameter <i>a</i> of the LC recursion.
		 */
		public final long a;

		/**
		 * The parameter <i>b</i> of the LC recursion.
		 */
		public final long b;

		/**
		 * Create a new parameter object.
		 *
		 * @param a the parameter <i>a</i> of the LC recursion.
		 * @param b the parameter <i>b</i> of the LC recursion.
		 */
		public Param(final long a, final long b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			return 31*(int)(a^(a >>> 32)) + 31*(int)(b^(b >>> 32));
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof Param)) {
				return false;
			}

			final Param param = (Param)obj;
			return a == param.a && b == param.b;
		}

		@Override
		public String toString() {
			return String.format("%s[a=%d, b=%d]", getClass().getName(), a, b);
		}
	}

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code LCG64ShiftRandom} PRNG.
	 *
	 * It's recommended to initialize the {@code RandomRegistry} the following
	 * way:
	 *
	 * [code]
	 * // Register the PRGN with the default parameters.
	 * RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
	 *
	 * // Register the PRNG with the {@code LECUYER3} parameters.
	 * RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal(
	 *     LCG64ShiftRandom.LECUYER3
	 * ));
	 * [/code]
	 *
	 * Be aware, that calls of the {@code setSeed(long)} method will throw an
	 * {@code UnsupportedOperationException} for <i>thread local</i> instances.
	 * [code]
	 * RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
	 *
	 * // Will throw 'UnsupportedOperationException'.
	 * RandomRegistry.getRandom().setSeed(1234);
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2012-12-12 $</em>
	 */
	public static class ThreadLocal extends java.lang.ThreadLocal<LCG64ShiftRandom> {
		private static final long STEP_BASE = 1L << 56;

		private int _block = 0;
		private long _seed = math.random.seed();

		private final Param _param;

		/**
		 * Create a new <i>thread local</i> instance of the
		 * {@code LCG64ShiftRandom} PRGN with the {@code DEFAULT} parameters.
		 */
		public ThreadLocal() {
			this(Param.DEFAULT);
		}

		/**
		 * Create a new <i>thread local</i> instance of the
		 * {@code LCG64ShiftRandom} PRGN with the given parameters.
		 *
		 * @param param the LC parameters.
		 * @throws NullPointerException if the given parameters are null.
		 */
		public ThreadLocal(final Param param) {
			_param = object.nonNull(param, "PRNG param must not be null.");
		}

		/**
		 * Create a new PRNG using <i>block splitting</i> for guaranteeing well
		 * distributed PRN for every thread.
		 *
		 * <p align="left">
		 * <strong>Tina’s Random Number Generator Library</strong>
		 * <br/>
		 * <em>Chapter 2. Pseudo-random numbers for parallel Monte Carlo
		 *     simulations, Page 7</em>
		 * <br/>
		 * <small>Heiko Bauke</small>
		 * <br/>
		 * [<a href="http://numbercrunch.de/trng/trng.pdf">
		 *  http://numbercrunch.de/trng/trng.pdf
		 *  </a>].
		 * <p/>
		 */
		@Override
		protected synchronized LCG64ShiftRandom initialValue() {
			if (_block > 127) {
				_block = 0;
				_seed = math.random.seed();
			}

			final LCG64ShiftRandom random = new TLLCG64ShiftRandom(_seed, _param);
			random.jump((_block++)*STEP_BASE);
			return random;
		}

	}

	private static final class TLLCG64ShiftRandom extends LCG64ShiftRandom {

		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLLCG64ShiftRandom(final long seed, final Param param) {
			super(seed, param);
		}

		@Override
		public void setSeed(final long seed) {
			if (_sentry != null) {
				throw new UnsupportedOperationException(
					"The 'setSeed(long)' method is not supported " +
					"for thread local instances."
				);
			}
		}

	}

	/**
	 * This is a <i>thread safe</i> variation of the this PRGN.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2012-12-12 $</em>
	 */
	public static class ThreadSafe extends LCG64ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * a safe seed.
		 */
		public ThreadSafe() {
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * the given seed.
		 *
		 * @param seed the seed of the PRNG
		 */
		public ThreadSafe(final long seed) {
			super(seed);
		}

		/**
		 * Create a new PRNG instance with the given parameter and a safe
		 * default seed.
		 *
		 * @param param the PRNG parameter.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final Param param) {
			super(param);
		}

		/**
		 * Create a new PRNG instance with the given parameter and seed.
		 *
		 * @param seed the seed of the PRNG.
		 * @param param the parameter of the PRNG.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final long seed, final Param param) {
			super(seed, param);
		}

		@Override
		public synchronized void setSeed(final long seed) {
			super.setSeed(seed);
		}

		@Override
		public synchronized long nextLong() {
			return super.nextLong();
		}
	}



	private final Param _param;
	private final long _seed;

	private long _a = 0;
	private long _b = 0;
	private long _r = 0;

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and safe
	 * seed.
	 */
	public LCG64ShiftRandom() {
		this(math.random.seed());
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
	 * given seed.
	 *
	 * @param seed the seed of the PRNG
	 */
	public LCG64ShiftRandom(final long seed) {
		this(seed, Param.DEFAULT);
	}

	/**
	 * Create a new PRNG instance with the given parameter and a safe seed
	 *
	 * @param param the PRNG parameter.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG64ShiftRandom(final Param param) {
		this(math.random.seed(), param);
	}

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param seed the seed of the PRNG.
	 * @param param the parameter of the PRNG.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG64ShiftRandom(final long seed, final Param param) {
		_param = object.nonNull(param, "PRNG param must not be null.");
		_seed = seed;

		_r = seed;
		_a = param.a;
		_b = param.b;
	}

	/**
	 * Resets the PRNG back to the creation state.
	 */
	public void reset() {
		_r = _seed;
		_a = _param.a;
		_b = _param.b;
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
	 * {@code [0, n)}. This method is mainly used for <i>parallelization</i>
	 * via <i>leapfrogging</i>.
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
			_a = math.pow(_a, p);
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

		_r = _r*math.pow(_a, 1L << s) + f(1L << s, _a)*_b;
	}

	/**
	 * Changes the internal state of the PRNG in such a way that the engine
	 * <i>jumps</i> s steps ahead.
	 *
	 * @param step the steps to jump ahead.
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

	@Override
	public String toString() {
		return String.format(
			"%s[a=%d, b=%d, r=%d",
			getClass().getName(), _a, _b, _r
		);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass())
				.and(_a).and(_b).and(_r)
				.and(_seed).and(_param).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LCG64ShiftRandom)) {
			return false;
		}

		final LCG64ShiftRandom random = (LCG64ShiftRandom)obj;
		return _a == random._a &&
				_b == random._b &&
				_r == random._r &&
				_seed == random._seed &&
				_param.equals(random._param);
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


