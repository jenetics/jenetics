/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.math.arithmetic;
import org.jenetics.internal.math.random;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * This class implements a linear congruential PRNG with additional bit-shift
 * transition. The base recursion
 * <p>
 * <img
 *     alt="r_{i+1} = (a\cdot r_i + b) \mod 2^{64}"
 *     src="doc-files/lcg-recursion.gif"
 * >
 * </p>
 * is followed by a non-linear transformation
 * <p>
 * <img
 *     alt="\begin{eqnarray*}
 *           t &=& r_i                \\
 *           t &=& t \oplus (t >> 17) \\
 *           t &=& t \oplus (t << 31) \\
 *           t &=& t \oplus (t >> 8)
 *         \end{eqnarray*}"
 *     src="doc-files/lcg-non-linear.gif"
 * >
 * </p>
 * which destroys the lattice structure introduced by the recursion. The period
 * of this PRNG is 2<sup>64</sup>, {@code iff} <i>b</i> is odd and <i>a</i>
 * {@code mod} 4 = 1.
 * <p>
 *
 * <em>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp">
 * trng::lcg64_shift</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library created by Heiko
 * Bauke.</em>
 *
 * <p>
 * <strong>Not that the base implementation of the {@code LCG64ShiftRandom}
 * class is not thread-safe.</strong> If multiple threads requests random
 * numbers from this class, it <i>must</i> be synchronized externally.
 * Alternatively you can use the thread-safe implementations
 * {@link LCG64ShiftRandom.ThreadSafe} or {@link LCG64ShiftRandom.ThreadLocal}.
 *
 * @see <a href="http://numbercrunch.de/trng/">TRNG</a>
 * @see RandomRegistry
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 2.0 &mdash; <em>$Date: 2014-12-22 $</em>
 */
public class LCG64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code LCG64ShiftRandom} PRNG.
	 *
	 * It's recommended to initialize the {@code RandomRegistry} the following
	 * way:
	 *
	 * [code]
	 * // Register the PRNG with the default parameters.
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
	 * @version 3.0 &mdash; <em>$Date: 2014-12-22 $</em>
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<LCG64ShiftRandom>
	{
		private static final long STEP_BASE = 1L << 56;

		private int _block = 0;
		private long _seed = random.seed();

		private final Param _param;

		/**
		 * Create a new <i>thread local</i> instance of the
		 * {@code LCG64ShiftRandom} PRNG with the {@code DEFAULT} parameters.
		 */
		public ThreadLocal() {
			this(Param.DEFAULT);
		}

		/**
		 * Create a new <i>thread local</i> instance of the
		 * {@code LCG64ShiftRandom} PRNG with the given parameters.
		 *
		 * @param param the LC parameters.
		 * @throws NullPointerException if the given parameters are null.
		 */
		public ThreadLocal(final Param param) {
			_param = requireNonNull(param, "PRNG param must not be null.");
		}

		/**
		 * Create a new PRNG using <i>block splitting</i> for guaranteeing well
		 * distributed PRN for every thread.
		 *
		 * <p>
		 * <strong>Tina’s Random Number Generator Library</strong>
		 * <br>
		 * <em>Chapter 2. Pseudo-random numbers for parallel Monte Carlo
		 *     simulations, Page 7</em>
		 * <br>
		 * <small>Heiko Bauke</small>
		 * <br>
		 * [<a href="http://numbercrunch.de/trng/trng.pdf">
		 *  http://numbercrunch.de/trng/trng.pdf
		 *  </a>].
		 */
		@Override
		protected synchronized LCG64ShiftRandom initialValue() {
			if (_block > 127) {
				_block = 0;
				_seed = random.seed();
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
			super(param, seed);
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
	 * This is a <i>thread safe</i> variation of the this PRNG&mdash;by
	 * synchronizing the random number generation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 3.0 &mdash; <em>$Date: 2014-12-22 $</em>
	 */
	public static final class ThreadSafe extends LCG64ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new PRNG instance with the given parameter and seed.
		 *
		 * @param seed the seed of the PRNG.
		 * @param param the parameter of the PRNG.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final long seed, final Param param) {
			super(param, seed);
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * the given seed.
		 *
		 * @param seed the seed of the PRNG
		 */
		public ThreadSafe(final long seed) {
			this(seed, Param.DEFAULT);
		}

		/**
		 * Create a new PRNG instance with the given parameter and a safe
		 * default seed.
		 *
		 * @param param the PRNG parameter.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final Param param) {
			this(random.seed(), param);
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * a safe seed.
		 */
		public ThreadSafe() {
			this(random.seed(), Param.DEFAULT);
		}

		@Override
		public synchronized void setSeed(final long seed) {
			super.setSeed(seed);
		}

		@Override
		public synchronized long nextLong() {
			return super.nextLong();
		}

		@Override
		public synchronized void split(final int p, final int s) {
			super.split(p, s);
		}

		@Override
		public synchronized void jump2(final int s) {
			super.jump2(s);
		}

		@Override
		public synchronized void jump(final long step) {
			super.jump(step);
		}

	}

	/**
	 * Parameter class for the {@code LCG64ShiftRandom} generator, for the
	 * parameters <i>a</i> and <i>b</i> of the LC recursion
	 * <i>r<sub>i+1</sub> = a · r<sub>i</sub> + b</i> mod <i>2<sup>64</sup></i>.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 2.0 &mdash; <em>$Date: 2014-12-22 $</em>
	 */
	public static final class Param implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * The default PRNG parameters: a = 0xFBD19FBBC5C07FF5L; b = 1
		 */
		public static final Param DEFAULT = Param.of(0xFBD19FBBC5C07FF5L, 1L);

		/**
		 * LEcuyer 1 parameters: a = 0x27BB2EE687B0B0FDL; b = 1
		 */
		public static final Param LECUYER1 = Param.of(0x27BB2EE687B0B0FDL, 1L);

		/**
		 * LEcuyer 2 parameters: a = 0x2C6FE96EE78B6955L; b = 1
		 */
		public static final Param LECUYER2 = Param.of(0x2C6FE96EE78B6955L, 1L);

		/**
		 * LEcuyer 3 parameters: a = 0x369DEA0F31A53F85L; b = 1
		 */
		public static final Param LECUYER3 = Param.of(0x369DEA0F31A53F85L, 1L);


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
		private Param(final long a, final long b) {
			this.a = a;
			this.b = b;
		}

		public static Param of(final long a, final long b) {
			return new Param(a, b);
		}

		@Override
		public int hashCode() {
			return 31*(int)(a^(a >>> 32)) + 31*(int)(b^(b >>> 32));
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(p -> a == p.a && b == p.b);
		}

		@Override
		public String toString() {
			return format("%s[a=%d, b=%d]", getClass().getName(), a, b);
		}
	}

	/**
	 * Represents the state of this random engine
	 */
	private final static class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			_r = seed;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass()).and(_r).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state -> state._r == _r);
		}

		@Override
		public String toString() {
			return format("State[%d]", _r);
		}
	}


	private Param _param;
	private final State _state;

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param param the parameter of the PRNG.
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG64ShiftRandom(final Param param, final long seed) {
		_param = requireNonNull(param, "PRNG param must not be null.");
		_state = new State(seed);
	}

	/**
	 * Create a new PRNG instance with the given parameter and a safe seed
	 *
	 * @param param the PRNG parameter.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG64ShiftRandom(final Param param) {
		this(param, random.seed());
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
	 * given seed.
	 *
	 * @param seed the seed of the PRNG
	 */
	public LCG64ShiftRandom(final long seed) {
		this(Param.DEFAULT, seed);
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and safe
	 * seed.
	 */
	public LCG64ShiftRandom() {
		this(Param.DEFAULT, random.seed());
	}

	@Override
	public long nextLong() {
		step();

		long t = _state._r;
		t ^= t >>> 17;
		t ^= t << 31;
		t ^= t >>> 8;
		return t;
	}

	private void step() {
		_state._r = _param.a*_state._r + _param.b;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	/**
	 * Changes the internal state of the PRNG in a way that future calls to
	 * {@link #nextLong()} will generated the s<sup>th</sup> sub-stream of
	 * p<sup>th</sup> sub-streams. <i>s</i> must be within the range of
	 * {@code [0, p-1)}. This method is mainly used for <i>parallelization</i>
	 * via <i>leap-frogging</i>.
	 *
	 * @param p the overall number of sub-streams
	 * @param s the s<sup>th</sup> sub-stream
	 * @throws IllegalArgumentException if {@code p < 1 || s >= p}.
	 */
	public void split(final int p, final int s) {
		if (p < 1) {
			throw new IllegalArgumentException(format(
				"p must be >= 1 but was %d.", p
			));
		}
		if (s >= p) {
			throw new IllegalArgumentException(format(
				"s must be < %d but was %d.", p, s
			));
		}

		if (p > 1) {
			jump(s + 1);
			final long b = _param.b*f(p, _param.a);
			final long a = arithmetic.pow(_param.a, p);
			_param = Param.of(a, b);
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
			throw new IllegalArgumentException(format(
				"s must be positive but was %d.", s
			));
		}

		if (s >= Long.SIZE) {
			throw new IllegalArgumentException(format(
				"The 'jump2' size must be smaller than %d but was %d.",
				Long.SIZE, s
			));
		}

		_state._r = _state._r*arithmetic.pow(_param.a, 1L << s) +
					f(1L << s, _param.a)*_param.b;
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
			throw new IllegalArgumentException(format(
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

	public Param getParam() {
		return _param;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_param)
			.and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_param, random._param) &&
			eq(_state, random._state)
		);
	}

	@Override
	public String toString() {
		return format("%s[%s, %s]", getClass().getSimpleName(), _param, _state);
	}



	/* *************************************************************************
	 * Some static helper methods
	 ***************************************************************************/

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

/*
#=============================================================================#
# Testing: org.jenetics.util.LCG64ShiftRandom (2014-12-22 20:06)              #
#=============================================================================#
#=============================================================================#
# Linux 3.16.0-28-generic (amd64)                                             #
# java version "1.8.0_25"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_25-b17)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.36e+07  | 995404065|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.88513397|  PASSED
      diehard_operm5|   0|   1000000|     100|0.62485774|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.25787172|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.82310594|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.25410565|  PASSED
        diehard_opso|   0|   2097152|     100|0.27994196|  PASSED
        diehard_oqso|   0|   2097152|     100|0.29423503|  PASSED
         diehard_dna|   0|   2097152|     100|0.94754044|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.54264055|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.22965023|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.45602493|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.78207294|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.04516364|  PASSED
     diehard_squeeze|   0|    100000|     100|0.90977421|  PASSED
        diehard_sums|   0|       100|     100|0.00384163|   WEAK
        diehard_runs|   0|    100000|     100|0.58757856|  PASSED
        diehard_runs|   0|    100000|     100|0.68126568|  PASSED
       diehard_craps|   0|    200000|     100|0.98405727|  PASSED
       diehard_craps|   0|    200000|     100|0.43748297|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.99909354|   WEAK
 marsaglia_tsang_gcd|   0|  10000000|     100|0.42705390|  PASSED
         sts_monobit|   1|    100000|     100|0.05277954|  PASSED
            sts_runs|   2|    100000|     100|0.74724764|  PASSED
          sts_serial|   1|    100000|     100|0.53431948|  PASSED
          sts_serial|   2|    100000|     100|0.45235557|  PASSED
          sts_serial|   3|    100000|     100|0.59547923|  PASSED
          sts_serial|   3|    100000|     100|0.13653712|  PASSED
          sts_serial|   4|    100000|     100|0.88745241|  PASSED
          sts_serial|   4|    100000|     100|0.75849526|  PASSED
          sts_serial|   5|    100000|     100|0.63956697|  PASSED
          sts_serial|   5|    100000|     100|0.50638398|  PASSED
          sts_serial|   6|    100000|     100|0.50970181|  PASSED
          sts_serial|   6|    100000|     100|0.74301220|  PASSED
          sts_serial|   7|    100000|     100|0.72536115|  PASSED
          sts_serial|   7|    100000|     100|0.42918461|  PASSED
          sts_serial|   8|    100000|     100|0.51745297|  PASSED
          sts_serial|   8|    100000|     100|0.27665980|  PASSED
          sts_serial|   9|    100000|     100|0.07219154|  PASSED
          sts_serial|   9|    100000|     100|0.16007881|  PASSED
          sts_serial|  10|    100000|     100|0.68864336|  PASSED
          sts_serial|  10|    100000|     100|0.64663821|  PASSED
          sts_serial|  11|    100000|     100|0.51544583|  PASSED
          sts_serial|  11|    100000|     100|0.04750204|  PASSED
          sts_serial|  12|    100000|     100|0.98324239|  PASSED
          sts_serial|  12|    100000|     100|0.73888233|  PASSED
          sts_serial|  13|    100000|     100|0.99796458|   WEAK
          sts_serial|  13|    100000|     100|0.57405237|  PASSED
          sts_serial|  14|    100000|     100|0.99820199|   WEAK
          sts_serial|  14|    100000|     100|0.74966215|  PASSED
          sts_serial|  15|    100000|     100|0.66130491|  PASSED
          sts_serial|  15|    100000|     100|0.27502970|  PASSED
          sts_serial|  16|    100000|     100|0.76403577|  PASSED
          sts_serial|  16|    100000|     100|0.77906323|  PASSED
         rgb_bitdist|   1|    100000|     100|0.85986918|  PASSED
         rgb_bitdist|   2|    100000|     100|0.19194081|  PASSED
         rgb_bitdist|   3|    100000|     100|0.37765353|  PASSED
         rgb_bitdist|   4|    100000|     100|0.40284131|  PASSED
         rgb_bitdist|   5|    100000|     100|0.63742901|  PASSED
         rgb_bitdist|   6|    100000|     100|0.81457600|  PASSED
         rgb_bitdist|   7|    100000|     100|0.98271991|  PASSED
         rgb_bitdist|   8|    100000|     100|0.74518833|  PASSED
         rgb_bitdist|   9|    100000|     100|0.25408860|  PASSED
         rgb_bitdist|  10|    100000|     100|0.71098196|  PASSED
         rgb_bitdist|  11|    100000|     100|0.32293476|  PASSED
         rgb_bitdist|  12|    100000|     100|0.64350515|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.61947836|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.60592190|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.25531102|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.69122761|  PASSED
    rgb_permutations|   2|    100000|     100|0.80529402|  PASSED
    rgb_permutations|   3|    100000|     100|0.02362682|  PASSED
    rgb_permutations|   4|    100000|     100|0.38540180|  PASSED
    rgb_permutations|   5|    100000|     100|0.41191808|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.10725403|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.28049411|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.94452071|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.24582457|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.32474902|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.62662596|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.05315903|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.87896717|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.49840401|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.34628462|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.60873298|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.55503249|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.04282574|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.91309214|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.34015015|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.13777151|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.94326616|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.55337305|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.07791838|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.88717640|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.46169416|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.28732211|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.73427824|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.74579333|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.94979002|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.90588183|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.37617442|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.86960028|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.80661374|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.57067702|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.97767659|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.96858026|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.03607615|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.07252636|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.13820055|  PASSED
             dab_dct| 256|     50000|       1|0.63195233|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.81023471|  PASSED
        dab_filltree|  32|  15000000|       1|0.64402067|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.94066606|  PASSED
       dab_filltree2|   1|   5000000|       1|0.06674646|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.49588718|  PASSED
#=============================================================================#
# Summary: PASSED=110, WEAK=4, FAILED=0                                       #
#          235,031.469 MB of random data created with 99.057 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:32                                                            #
#=============================================================================#
*/
