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
import static org.jenetics.util.object.hashCodeOf;

import java.io.Serializable;


/**
 * This class implements a linear congruential PRNG with additional bit-shift
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
 * which destroys the lattice structure introduced by the recursion. The period
 * of this PRNG is 2<sup>64</sup>, {@code iff} <i>b</i> is odd and <i>a</i>
 * {@code mod} 4 = 1.
 * <p/>
 *
 * <em>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp">
 * trng::lcg64_shift</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library created by Heiko
 * Bauke.</em>
 *
 * <p/>
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
 * @version 1.1 &mdash; <em>$Date: 2013-11-23 $</em>
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
	 * @version 1.1 &mdash; <em>$Date: 2013-11-23 $</em>
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
		 * LEcuyer 2 parameters: a = 0x2C6FE96EE78B6955L; b = 1
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
			return format("%s[a=%d, b=%d]", getClass().getName(), a, b);
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
	 * @version 1.1 &mdash; <em>$Date: 2013-11-23 $</em>
	 */
	public static class ThreadLocal
		extends java.lang.ThreadLocal<LCG64ShiftRandom>
	{
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
			_param = requireNonNull(param, "PRNG param must not be null.");
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
	 * This is a <i>thread safe</i> variation of the this PRGN&mdash;by
	 * synchronizing the random number generation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2013-11-23 $</em>
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
		public synchronized void reset() {
			super.reset();
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
		_param = requireNonNull(param, "PRNG param must not be null.");
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

	@Override
	public String toString() {
		return format(
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

/*
#=============================================================================#
# Testing: org.jenetics.util.LCG64ShiftRandom                                 #
#=============================================================================#
#=============================================================================#
# Linux 3.11.0-13-generic (amd64)                                             #
# java version "1.7.0_45"                                                     #
# Java(TM) SE Runtime Environment (build 1.7.0_45-b18)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 24.45-b08)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.70e+07  |1339094149|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.95423793|  PASSED
      diehard_operm5|   0|   1000000|     100|0.74664512|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.28276445|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.98995522|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.62136891|  PASSED
        diehard_opso|   0|   2097152|     100|0.11489927|  PASSED
        diehard_oqso|   0|   2097152|     100|0.49614031|  PASSED
         diehard_dna|   0|   2097152|     100|0.82189883|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.08804388|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.03511168|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.72787140|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.63108013|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.67512583|  PASSED
     diehard_squeeze|   0|    100000|     100|0.92282176|  PASSED
        diehard_sums|   0|       100|     100|0.25982688|  PASSED
        diehard_runs|   0|    100000|     100|0.90178166|  PASSED
        diehard_runs|   0|    100000|     100|0.44356919|  PASSED
       diehard_craps|   0|    200000|     100|0.88419986|  PASSED
       diehard_craps|   0|    200000|     100|0.02856124|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.59400971|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.18396288|  PASSED
         sts_monobit|   1|    100000|     100|0.80870435|  PASSED
            sts_runs|   2|    100000|     100|0.32227549|  PASSED
          sts_serial|   1|    100000|     100|0.16629803|  PASSED
          sts_serial|   2|    100000|     100|0.32473595|  PASSED
          sts_serial|   3|    100000|     100|0.21303527|  PASSED
          sts_serial|   3|    100000|     100|0.12318414|  PASSED
          sts_serial|   4|    100000|     100|0.20441948|  PASSED
          sts_serial|   4|    100000|     100|0.36511213|  PASSED
          sts_serial|   5|    100000|     100|0.79577994|  PASSED
          sts_serial|   5|    100000|     100|0.73588190|  PASSED
          sts_serial|   6|    100000|     100|0.44202681|  PASSED
          sts_serial|   6|    100000|     100|0.39719117|  PASSED
          sts_serial|   7|    100000|     100|0.02427815|  PASSED
          sts_serial|   7|    100000|     100|0.06474928|  PASSED
          sts_serial|   8|    100000|     100|0.17585941|  PASSED
          sts_serial|   8|    100000|     100|0.91955351|  PASSED
          sts_serial|   9|    100000|     100|0.85518847|  PASSED
          sts_serial|   9|    100000|     100|0.89546403|  PASSED
          sts_serial|  10|    100000|     100|0.51709386|  PASSED
          sts_serial|  10|    100000|     100|0.65833950|  PASSED
          sts_serial|  11|    100000|     100|0.93618281|  PASSED
          sts_serial|  11|    100000|     100|0.55457518|  PASSED
          sts_serial|  12|    100000|     100|0.50675571|  PASSED
          sts_serial|  12|    100000|     100|0.27773041|  PASSED
          sts_serial|  13|    100000|     100|0.56281286|  PASSED
          sts_serial|  13|    100000|     100|0.45462349|  PASSED
          sts_serial|  14|    100000|     100|0.93401610|  PASSED
          sts_serial|  14|    100000|     100|0.42606578|  PASSED
          sts_serial|  15|    100000|     100|0.68207778|  PASSED
          sts_serial|  15|    100000|     100|0.56797292|  PASSED
          sts_serial|  16|    100000|     100|0.64586881|  PASSED
          sts_serial|  16|    100000|     100|0.46819688|  PASSED
         rgb_bitdist|   1|    100000|     100|0.75299060|  PASSED
         rgb_bitdist|   2|    100000|     100|0.67254946|  PASSED
         rgb_bitdist|   3|    100000|     100|0.02449607|  PASSED
         rgb_bitdist|   4|    100000|     100|0.89528714|  PASSED
         rgb_bitdist|   5|    100000|     100|0.85263526|  PASSED
         rgb_bitdist|   6|    100000|     100|0.49239255|  PASSED
         rgb_bitdist|   7|    100000|     100|0.83800165|  PASSED
         rgb_bitdist|   8|    100000|     100|0.36559577|  PASSED
         rgb_bitdist|   9|    100000|     100|0.63252223|  PASSED
         rgb_bitdist|  10|    100000|     100|0.57858162|  PASSED
         rgb_bitdist|  11|    100000|     100|0.28260180|  PASSED
         rgb_bitdist|  12|    100000|     100|0.79319322|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.21289201|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.20894816|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.62765001|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.18703207|  PASSED
    rgb_permutations|   2|    100000|     100|0.72914161|  PASSED
    rgb_permutations|   3|    100000|     100|0.74275457|  PASSED
    rgb_permutations|   4|    100000|     100|0.07965664|  PASSED
    rgb_permutations|   5|    100000|     100|0.50494408|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.54086607|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.16102067|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.19759235|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.75269964|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.86615992|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.99266261|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.32768603|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.70149106|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.91189774|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.54448404|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.31758536|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.26925707|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.90902693|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.74775510|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.91153343|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.65460110|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.53729599|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.21807145|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.54525836|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.29688609|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.30695225|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.68960134|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.61609910|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.16369122|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.49562910|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.33804318|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.97370987|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.73694708|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.21192565|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.54936029|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.94390594|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.80185863|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.11853379|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.94869308|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.99444780|  PASSED
             dab_dct| 256|     50000|       1|0.27214190|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.64388779|  PASSED
        dab_filltree|  32|  15000000|       1|0.31640103|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.87984154|  PASSED
       dab_filltree2|   1|   5000000|       1|0.23987775|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.32626907|  PASSED
#=============================================================================#
# Runtime: 0:36:48                                                            #
#=============================================================================#
*/

