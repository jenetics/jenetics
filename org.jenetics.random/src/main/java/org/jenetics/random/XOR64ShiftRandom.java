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
package org.jenetics.random;

import static org.jenetics.random.internal.util.Equality.eq;

import org.jenetics.random.internal.util.Equality;
import org.jenetics.random.internal.util.Hash;

/**
 * <p><em>
 * This generator was discovered and characterized by George Marsaglia
 * [<a href="http://www.jstatsoft.org/v08/i14/paper">Xorshift RNGs</a>]. In just
 * three XORs and three shifts (generally fast operations) it produces a full
 * period of 2<sup>64</sup> - 1 on 64 bits. (The missing value is zero, which
 * perpetuates itself and must be avoided.) High and low bits pass Diehard.
 * </em>
 *
 * <p>
 * <strong>Numerical Recipes 3rd Edition: The Art of Scientific Computing</strong>
 * <br>
 * <em>Chapter 7. Random Numbers, Section 7.1.2, Page 345</em>
 * <br>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br>
 * ISBN:0521880688 9780521880688
 * <br>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 *
 * <p><b>
 * The <i>main</i> class of this PRNG is not thread safe. To create an thread
 * safe instances of this PRNG, use the {@link XOR64ShiftRandom.ThreadSafe} class.
 * </b></p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class XOR64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;


	/**
	 * [code]
	 * RandomRegistry.setRandom(new XORShiftRandom.ThreadLocal());
	 * [/code]
	 *
	 * Calling the {@link XOR64ShiftRandom#setSeed(long)} method on the returned
	 * instance will throw an {@link UnsupportedOperationException}.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<XOR64ShiftRandom>
	{

		private final long _seed = math.seed();

		@Override
		protected XOR64ShiftRandom initialValue() {
			return new TLXOR64ShiftRandom(math.seed(_seed));
		}
	}

	private static final class TLXOR64ShiftRandom extends XOR64ShiftRandom {

		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLXOR64ShiftRandom(final long seed) {
			super(seed);
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
	 * This class is a <i>thread safe</i> version of the {@code XORShiftRandom}
	 * engine. Instances of <i>this</i> class and instances of the non-thread
	 * safe variants, with the same seed, will generate the same sequence of
	 * random numbers.
	 * [code]
	 * final XORShiftRandom a = new XORShiftRandom(123);
	 * final XORShiftRandom b = XORShiftRandom.ThreadSafe(123);
	 * for (int i = 0; i &lt; 1000;  ++i) {
	 *     assert (a.nextLong() == b.nextLong());
	 *     assert (a.nextDouble() == b.nextDouble());
	 * }
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1
	 */
	public static final class ThreadSafe extends XOR64ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new <i>thread safe</i> instance of the XOR-Shift PRNG, with
		 * an seed of {@link System#nanoTime()}.
		 */
		public ThreadSafe() {
		}

		/**
		 * Create a new <i>thread safe</i> instance of the XOR-Shift PRNG.
		 *
		 * @param seed the seed of the PRNG.
		 */
		public ThreadSafe(final long seed) {
			super(seed);
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

	private long _x;

	/**
	 * Create a new <i>non-thread safe</i> instance of the XOR-Shift PRNG, with
	 * an seed of {@link System#nanoTime()}.
	 */
	public XOR64ShiftRandom() {
		this(math.seed());
	}

	/**
	 * Create a new <i>non-thread safe</i> instance of the XOR-Shift PRNG.
	 *
	 * @param seed the seed of the PRNG.
	 */
	public XOR64ShiftRandom(final long seed) {
		_x = init(seed);
	}

	private static long init(final long seed) {
		return seed == 0 ? 0xdeadbeef : seed;
	}

	@Override
	public long nextLong() {
//		The list of the suggested shift values:
//		21, 35, 4
//		20, 41, 5
//		17, 31, 8
//		11, 29, 14
//		14, 29, 11
//		30, 35, 13
//		21, 37, 4
//		21, 43, 4
//		23, 41, 18

		_x ^= (_x << 21);
		_x ^= (_x >>> 35);
		_x ^= (_x << 4);
		return _x;
	}

	@Override
	public void setSeed(final long seed) {
		_x = init(seed);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_x).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_x, random._x)
		);
	}

	@Override
	public String toString() {
		return String.format("%s[%d]", getClass().getName(), _x);
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.XOR64ShiftRandom (2014-07-28 05:48)             #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-32-generic (amd64)                                             #
# java version "1.8.0_11"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_11-b12)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.34e+07  |3717427707|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.74420300|  PASSED
      diehard_operm5|   0|   1000000|     100|0.65735635|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.30744907|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.46399825|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.68285102|  PASSED
        diehard_opso|   0|   2097152|     100|0.27578282|  PASSED
        diehard_oqso|   0|   2097152|     100|0.32370046|  PASSED
         diehard_dna|   0|   2097152|     100|0.76744898|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.86118067|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.75128284|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.50512183|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.51593572|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.87755581|  PASSED
     diehard_squeeze|   0|    100000|     100|0.96718419|  PASSED
        diehard_sums|   0|       100|     100|0.35411134|  PASSED
        diehard_runs|   0|    100000|     100|0.58802300|  PASSED
        diehard_runs|   0|    100000|     100|0.26862294|  PASSED
       diehard_craps|   0|    200000|     100|0.57037509|  PASSED
       diehard_craps|   0|    200000|     100|0.06964936|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.84226736|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.84079673|  PASSED
         sts_monobit|   1|    100000|     100|0.69066679|  PASSED
            sts_runs|   2|    100000|     100|0.65843367|  PASSED
          sts_serial|   1|    100000|     100|0.89635992|  PASSED
          sts_serial|   2|    100000|     100|0.77746717|  PASSED
          sts_serial|   3|    100000|     100|0.38430255|  PASSED
          sts_serial|   3|    100000|     100|0.03546115|  PASSED
          sts_serial|   4|    100000|     100|0.99050933|  PASSED
          sts_serial|   4|    100000|     100|0.27005796|  PASSED
          sts_serial|   5|    100000|     100|0.91274323|  PASSED
          sts_serial|   5|    100000|     100|0.64941520|  PASSED
          sts_serial|   6|    100000|     100|0.32001266|  PASSED
          sts_serial|   6|    100000|     100|0.16856479|  PASSED
          sts_serial|   7|    100000|     100|0.77910093|  PASSED
          sts_serial|   7|    100000|     100|0.16568580|  PASSED
          sts_serial|   8|    100000|     100|0.82776241|  PASSED
          sts_serial|   8|    100000|     100|0.69618914|  PASSED
          sts_serial|   9|    100000|     100|0.34231816|  PASSED
          sts_serial|   9|    100000|     100|0.90320916|  PASSED
          sts_serial|  10|    100000|     100|0.98914131|  PASSED
          sts_serial|  10|    100000|     100|0.72574827|  PASSED
          sts_serial|  11|    100000|     100|0.70333826|  PASSED
          sts_serial|  11|    100000|     100|0.20869402|  PASSED
          sts_serial|  12|    100000|     100|0.51565187|  PASSED
          sts_serial|  12|    100000|     100|0.20953489|  PASSED
          sts_serial|  13|    100000|     100|0.82587000|  PASSED
          sts_serial|  13|    100000|     100|0.69865824|  PASSED
          sts_serial|  14|    100000|     100|0.64731851|  PASSED
          sts_serial|  14|    100000|     100|0.92203060|  PASSED
          sts_serial|  15|    100000|     100|0.72361302|  PASSED
          sts_serial|  15|    100000|     100|0.52368580|  PASSED
          sts_serial|  16|    100000|     100|0.12722024|  PASSED
          sts_serial|  16|    100000|     100|0.63121444|  PASSED
         rgb_bitdist|   1|    100000|     100|0.74784868|  PASSED
         rgb_bitdist|   2|    100000|     100|0.76749839|  PASSED
         rgb_bitdist|   3|    100000|     100|0.16618605|  PASSED
         rgb_bitdist|   4|    100000|     100|0.47985583|  PASSED
         rgb_bitdist|   5|    100000|     100|0.99325127|  PASSED
         rgb_bitdist|   6|    100000|     100|0.49335029|  PASSED
         rgb_bitdist|   7|    100000|     100|0.64743489|  PASSED
         rgb_bitdist|   8|    100000|     100|0.16878796|  PASSED
         rgb_bitdist|   9|    100000|     100|0.52120474|  PASSED
         rgb_bitdist|  10|    100000|     100|0.30783035|  PASSED
         rgb_bitdist|  11|    100000|     100|0.17761453|  PASSED
         rgb_bitdist|  12|    100000|     100|0.60713960|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.85325239|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.01562370|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.77140626|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.19604161|  PASSED
    rgb_permutations|   2|    100000|     100|0.12513471|  PASSED
    rgb_permutations|   3|    100000|     100|0.09012698|  PASSED
    rgb_permutations|   4|    100000|     100|0.87087856|  PASSED
    rgb_permutations|   5|    100000|     100|0.36319706|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.63423139|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.86684803|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.37897989|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.31372943|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.41004609|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.56227712|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.37271211|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.72491443|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.31195418|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.51720517|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.65635509|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.79332100|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.85115196|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.14822030|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.61822912|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.99571844|   WEAK
      rgb_lagged_sum|  16|   1000000|     100|0.68048942|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.61180053|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.45222379|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.76776256|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.88631467|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.37689406|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.42289065|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.13038981|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.40195367|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.31100786|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.20605908|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.31923701|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.28780381|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.01134830|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.85833103|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.86715461|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.95437310|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.04869521|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.69091492|  PASSED
             dab_dct| 256|     50000|       1|0.13048426|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.88014342|  PASSED
        dab_filltree|  32|  15000000|       1|0.77963133|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.67110514|  PASSED
       dab_filltree2|   1|   5000000|       1|0.83858438|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED=112, WEAK=1, FAILED=1                                       #
#          235,031.281 MB of random data created with 106.797 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:36:40                                                            #
#=============================================================================#
*/
