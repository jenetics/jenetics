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
package org.jenetix.random;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.Random;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random32;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-11 $</em>
 */
public class LCG32Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * Parameter class for the {@code LCG64ShiftRandom} generator, for the
	 * parameters <i>a</i> and <i>b</i> of the LC recursion
	 * <i>r<sub>i+1</sub> = a · r<sub>i</sub> + b</i> mod <i>2<sup>64</sup></i>.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1 &mdash; <em>$Date: 2014-07-11 $</em>
	 */
	public static final class Param implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * The default PRNG parameters: a = 0xFBD19FBBC5C07FF5L; b = 1
		 */
		public static final Param DEFAULT = new Param(0xFBD19FB, 1);

		/**
		 * LEcuyer 1 parameters: a = 0x27BB2EE687B0B0FDL; b = 1
		 */
		public static final Param LECUYER1 = new Param(0x27BB2E, 1);

		/**
		 * LEcuyer 2 parameters: a = 0x2C6FE96EE78B6955L; b = 1
		 */
		public static final Param LECUYER2 = new Param(0x2C6FE9, 1);

		/**
		 * LEcuyer 3 parameters: a = 0x369DEA0F31A53F85L; b = 1
		 */
		public static final Param LECUYER3 = new Param(0x369DEA, 1);


		/**
		 * The parameter <i>a</i> of the LC recursion.
		 */
		public final int a;

		/**
		 * The parameter <i>b</i> of the LC recursion.
		 */
		public final int b;

		/**
		 * Create a new parameter object.
		 *
		 * @param a the parameter <i>a</i> of the LC recursion.
		 * @param b the parameter <i>b</i> of the LC recursion.
		 */
		public Param(final int a, final int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			return a^b;
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test( param ->
				eq(a, param.a) &&
				eq(b, param.b)
			);
		}

		@Override
		public String toString() {
			return String.format("%s[a=%d, b=%d]", getClass().getName(), a, b);
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
	public LCG32Random() {
		this(math.random.seed());
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
	 * given seed.
	 *
	 * @param seed the seed of the PRNG
	 */
	public LCG32Random(final long seed) {
		this(seed, Param.DEFAULT);
	}

	/**
	 * Create a new PRNG instance with the given parameter and a safe seed
	 *
	 * @param param the PRNG parameter.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG32Random(final Param param) {
		this(math.random.seed(), param);
	}

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param seed the seed of the PRNG.
	 * @param param the parameter of the PRNG.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG32Random(final long seed, final Param param) {
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
	public int nextInt() {
		step();

		long t = _r;
		t ^= t >>> 17;
		t ^= t << 31;
		t ^= t >>> 8;
		return (int)(t >>> Integer.SIZE);
	}

	private void step() {
		_r = _a*_r + _b;
	}

	/**
	 * Changes the internal state of the PRNG in a way that future calls to
	 * {@link #nextLong()} will generated the s<sup>th</sup> sub-stream of
	 * p<sup>th</sup> sub-streams. <i>s</i> must be within the range of
	 * {@code [0, p-1)}. This method is mainly used for <i>parallelization</i>
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
		return Hash.of(getClass())
				.and(_a)
				.and(_b)
				.and(_r)
				.and(_seed)
				.and(_param).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_a, random._a) &&
			eq(_b, random._b) &&
			eq(_r, random._r) &&
			eq(_seed, random._seed) &&
			eq(_param, random._param)
		);
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


	public static void main(final String[] args) {
		final Random random = new LCG32Random();
		random.ints().limit(10).forEach(System.out::println);
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.LCG32Random (2014-07-11 22:20)                  #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-30-generic (amd64)                                             #
# java version "1.8.0_05"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_05-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.5-b02)                          #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.05e+07  |3382420719|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.30611733|  PASSED
      diehard_operm5|   0|   1000000|     100|0.19288897|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.86668015|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.11369191|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.41753245|  PASSED
        diehard_opso|   0|   2097152|     100|0.78737260|  PASSED
        diehard_oqso|   0|   2097152|     100|0.57663445|  PASSED
         diehard_dna|   0|   2097152|     100|0.07856355|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.96082854|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.99982295|   WEAK
 diehard_parking_lot|   0|     12000|     100|0.05056232|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.70977062|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.91187869|  PASSED
     diehard_squeeze|   0|    100000|     100|0.78621989|  PASSED
        diehard_sums|   0|       100|     100|0.24916582|  PASSED
        diehard_runs|   0|    100000|     100|0.98949637|  PASSED
        diehard_runs|   0|    100000|     100|0.14282352|  PASSED
       diehard_craps|   0|    200000|     100|0.34857906|  PASSED
       diehard_craps|   0|    200000|     100|0.91260910|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.23458076|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.71856381|  PASSED
         sts_monobit|   1|    100000|     100|0.77417311|  PASSED
            sts_runs|   2|    100000|     100|0.09681315|  PASSED
          sts_serial|   1|    100000|     100|0.43164028|  PASSED
          sts_serial|   2|    100000|     100|0.79054599|  PASSED
          sts_serial|   3|    100000|     100|0.99692468|   WEAK
          sts_serial|   3|    100000|     100|0.50155464|  PASSED
          sts_serial|   4|    100000|     100|0.62933643|  PASSED
          sts_serial|   4|    100000|     100|0.68603569|  PASSED
          sts_serial|   5|    100000|     100|0.99083700|  PASSED
          sts_serial|   5|    100000|     100|0.94342767|  PASSED
          sts_serial|   6|    100000|     100|0.24347683|  PASSED
          sts_serial|   6|    100000|     100|0.11263825|  PASSED
          sts_serial|   7|    100000|     100|0.79862695|  PASSED
          sts_serial|   7|    100000|     100|0.50875718|  PASSED
          sts_serial|   8|    100000|     100|0.79543375|  PASSED
          sts_serial|   8|    100000|     100|0.65912375|  PASSED
          sts_serial|   9|    100000|     100|0.80531819|  PASSED
          sts_serial|   9|    100000|     100|0.46484191|  PASSED
          sts_serial|  10|    100000|     100|0.26644124|  PASSED
          sts_serial|  10|    100000|     100|0.56605379|  PASSED
          sts_serial|  11|    100000|     100|0.77126801|  PASSED
          sts_serial|  11|    100000|     100|0.42339889|  PASSED
          sts_serial|  12|    100000|     100|0.76570754|  PASSED
          sts_serial|  12|    100000|     100|0.75337660|  PASSED
          sts_serial|  13|    100000|     100|0.99020676|  PASSED
          sts_serial|  13|    100000|     100|0.81899791|  PASSED
          sts_serial|  14|    100000|     100|0.49259944|  PASSED
          sts_serial|  14|    100000|     100|0.83577761|  PASSED
          sts_serial|  15|    100000|     100|0.60465363|  PASSED
          sts_serial|  15|    100000|     100|0.95786405|  PASSED
          sts_serial|  16|    100000|     100|0.72449133|  PASSED
          sts_serial|  16|    100000|     100|0.72056369|  PASSED
         rgb_bitdist|   1|    100000|     100|0.72723260|  PASSED
         rgb_bitdist|   2|    100000|     100|0.46325634|  PASSED
         rgb_bitdist|   3|    100000|     100|0.70944132|  PASSED
         rgb_bitdist|   4|    100000|     100|0.11735293|  PASSED
         rgb_bitdist|   5|    100000|     100|0.66074713|  PASSED
         rgb_bitdist|   6|    100000|     100|0.90043319|  PASSED
         rgb_bitdist|   7|    100000|     100|0.25921363|  PASSED
         rgb_bitdist|   8|    100000|     100|0.29889644|  PASSED
         rgb_bitdist|   9|    100000|     100|0.78697439|  PASSED
         rgb_bitdist|  10|    100000|     100|0.42300929|  PASSED
         rgb_bitdist|  11|    100000|     100|0.19579019|  PASSED
         rgb_bitdist|  12|    100000|     100|0.80490661|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.47229588|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.56196026|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.96143695|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.23509376|  PASSED
    rgb_permutations|   2|    100000|     100|0.12244731|  PASSED
    rgb_permutations|   3|    100000|     100|0.51822298|  PASSED
    rgb_permutations|   4|    100000|     100|0.38610446|  PASSED
    rgb_permutations|   5|    100000|     100|0.48687513|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.23242223|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.36204089|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.22446124|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.74385103|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.73261872|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.12288291|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.57337025|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.92681359|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.01228896|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.11243932|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.93089268|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.67836685|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.68855997|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.98982080|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.62694612|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.31902284|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.18564337|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.77144177|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.82362143|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.26536502|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.96865845|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.83966978|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.16302380|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.35047586|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.21579471|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.18329969|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.91612892|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.89091227|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.32843196|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.59563219|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.96646667|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.97103975|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.50264641|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.56380064|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.97145720|  PASSED
             dab_dct| 256|     50000|       1|0.55942416|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.50027241|  PASSED
        dab_filltree|  32|  15000000|       1|0.49327503|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.44224884|  PASSED
       dab_filltree2|   1|   5000000|       1|0.22241194|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.99184300|  PASSED
#=============================================================================#
# Runtime: 0:40:04                                                            #
#=============================================================================#
*/
